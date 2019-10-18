(ns chapter5.examples
  (:require [chapter5.data :refer :all]

            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.core.reducers :as r]

            [incanter.core :as i]
            [incanter.charts :as c]

            [iota]     
       
            [tesser.math :as m]
            [tesser.core :as t]
            [tesser.hadoop :as h]

            [parkour.io.text :as text]
            [parkour.conf :as conf]
  )
)

(defn ex-5-1
  []
  (-> (slurp "data/soi.csv")
      (str/split #"\n")
      (first)
  )
)

(defn ex-5-2
  []
  (-> (io/reader "data/soi.csv")
      (line-seq)
      (first)
  )
)

(defn soi-line-seq
  []
  (-> (io/reader "data/soi.csv")
      (line-seq)
  )
)

(defn ex-5-3
  []
  (-> (soi-line-seq)
      (count)
  )
)


(defn ex-5-4
  []
  (->> (soi-line-seq)
       (reduce (fn [i x] (inc i)) 0)
  )
)



(defn ex-5-5
  []
  (->> (soi-line-seq)
       (r/fold + (fn [i x] (inc i)))
  )
)


(defn ex-5-6
  []
  (println "(+):" (+))
  (println "(*):" (*))
)


(defn ex-5-7
  []
  (->> (iota/seq "data/soi.csv")
      (r/fold + (fn [i x] (inc i)))
  )
)

(defn ex-5-8
  []
  (->> (iota/seq "data/soi.csv")
      (r/drop 1)
      (r/map parse-line)
      (r/take 1)
      (into [])
  )
)


(defn ex-5-9
  []
  (let [data (iota/seq "data/soi.csv")
        header (parse-header (first data))
       ]
     (->> (r/drop 1 data)
          (r/map parse-line)
          (r/map (fn [fields]
                     (zipmap header fields)
                 )
          )
          (r/take 1)
          (into [])
     )
  )
)



(defn ex-5-10
  [data]
  (->> (r/remove (fn [record]
                   (zero? (:zipcode record))
                 ) 
                 data
      )
      (r/take 1)
      (into [])
  )
)

(defn ex-5-11
  [data]
  (let [xs (into [] (r/map :N1 data))]
       (/ (reduce + xs)
          (count xs)
       )
  )
)


(defn mean
  ([] 0)
  ([x y] (/ (+ x y) 2))
)

(defn ex-5-12
  [data]
  (->> (r/map :N1 data)
       (r/fold mean)
  )
)

(defn mean-combiner
  ([] {:count 0 :sum 0})
  ([x y] (merge-with + x y))
)

(defn mean-reducer
  [acc x] 
  (-> acc
      (update-in [:count] inc)
      (update-in [:sum] + x)
  )
)

(defn ex-5-13
  [data]
  (->> (r/map :N1 data)
       (r/fold mean-combiner mean-reducer)
  )
)

(defn mean-post-combiner
  [{:keys [count sum]}] 
  (if (zero? count)
      0 
      (/ sum count)
  )
)

(defn ex-5-14
  [data]
  (->> (r/map :N1 data)
       (r/fold mean-combiner mean-reducer)
       (mean-post-combiner)
  )
)

(defn ex-5-15
  [data]
  (let [data (r/map :N1 data)
        mean-x (->> data
                    (r/fold mean-combiner mean-reducer)
                    (mean-post-combiner)
               )
        sq-diff (fn [x] (i/pow (- x mean-x) 2))
       ]

      (->> data
           (r/map sq-diff)
           (r/fold mean-combiner mean-reducer)
           (mean-post-combiner)
      )
   )
)

(defn variance-combiner
   ([] {:count 0 :sum 0 :sum2 0})
   ([a b]
    (let [count (+ (:count a) (:count b))
          sum (+ (:sum a) (:sum b))
          sum2 (+ (:sum2 a) (:sum2 b))]
          {:count count :sum sum :sum2 sum}
    )
  )
)

(defn variance-reducer
  [{:keys [count sum sum2]} x]
  {:count (inc count) :sum (+ sum x) :sum2 (+ sum2 (i/pow x 2))}
)


(defn variance-post-combiner
 [{:keys [count sum sum2]}]
 (if (zero? count) 
     0
     (- (/ sum2 count) (i/pow (/ sum count)))
 )
)   

(defn ex-5-16
  [data]
  (->> (r/map :N1 data)
       (r/fold variance-combiner variance-reducer)
       (variance-post-combiner)
  )
)

(defn ex-5-17
  [data]
  (let [data (into [] data)]
       (->> (m/covariance :A02300 :A00200)
            (t/tesser (t/chunk 512 data))
       )
  )
)

(defn chunks
  [coll]
  (->> (into [] coll)
       (t/chunk 1024)
  )
)

(defn ex-5-18
  []
  (let [data (iota/seq "data/soi.csv")]
       (->> (prepare-data)
            (m/covariance :A02300 :A00200)
            (t/tesser (chunks data))
       )
  )
)


(defn ex-5-19
  []
  (let [data (iota/seq "data/soi.csv")]
       (->> (prepare-data)
            (m/correlation :A02300 :A00200)
            (t/tesser (chunks data))
       )
  )
)
   


(defn ex-5-20
  []
  (let [data (iota/seq "data/soi.csv")]
       (->> (prepare-data)
            (t/map :A00200)
            (t/fuse {:A00200-mean (m/mean)
                     :A00200-sd (m/standard-deviation)})
            (t/tesser (chunks data))
       )
  )
)

(defn ex-5-21
  []
  (let [data (iota/seq "data/soi.csv")]
       (->> (prepare-data)
            (t/map #(select-keys % [:A00200 :A02300]))
            (t/facet)
            (m/mean)
            (t/tesser (chunks data))
       )
  )
)

(defn calculate-coefficients 
  [{:keys [covariance variance-x mean-x mean-y]}]
  (let [slope (/ covariance variance-x)
        intercept (- mean-y (* mean-x slope))]
       [intercept slope]
  )
)

(defn ex-5-22
  []
  (let [data (iota/seq "data/soi.csv")
        fx :A00200
        fy :A02300]
       (->> (prepare-data)
            (t/fuse {:covariance (m/covariance fx fy)
                     :variance-x (m/variance (t/map fx))
                     :mean-x (m/mean (t/map fx))
                     :mean-y (m/mean (t/map fy))
                    })
            (t/post-combine calculate-coefficients)
            (t/tesser (chunks data))
       )
  )
)

(defn ex-5-23
  []
  (let [data (iota/seq "data/soi.csv")
        attributes {:unemp-compensation :A02300
                    :salary             :A00200
                    :gross-income       :AGI_STUB
                    :joint-submission   :MARS2
                    :dependents         :NUMDEP}]
       (->> (prepare-data)
            (m/correlation-matrix attributes)
            (t/tesser (chunks data))
       )
  )
)

(defn feature-scales 
    [features]
    (->> (prepare-data)
         (t/map #(select-keys % features))
         (t/facet)
         (t/fuse {:mean (m/mean)
                  :sd (m/standard-deviation)})
    )
)

(defn ex-5-24
  []
  (let [data (iota/seq "data/soi.csv")
        features [:A02300 :A00200 :AGI_STUB :MARS2 :NUMDEP]]
       (->> (feature-scales features)
            (t/tesser (chunks data))
       )
  )
)

(defn scale-features 
    [factors]
    (let [f (fn [x {:keys [mean sd]}]
                (/ (- x mean) sd)
            )
         ] 
         (fn [x]
           (merge-with f x factors)
         )
    )
)

(defn unscale-features 
    [factors]
    (let [f (fn [x {:keys [mean sd]}]
                (+ (* x sd) mean)
            )
         ] 
         (fn [x]
           (merge-with f x factors)
         )
    )
)


(defn ex-5-25
  []
  (let [data (iota/seq "data/soi.csv")
        features [:A02300 :A00200 :AGI_STUB :MARS2 :NUMDEP]
        factors (->> (feature-scales features)
                     (t/tesser (chunks data))
                )
       ]
       (->> (soi-data)
            (r/remove  #(zero? (:zipcode %)))
            (r/map #(select-keys % features))
            (r/map (scale-features factors))
            (into [])
            (first)
       )
  )
)    

(defn feature-matrix 
   [record features]
   (let [xs (map #(% record) features)]
        (i/matrix (cons 1 xs))
   )
)

(defn extract-features
   [fy features]
   (fn [record]
       {:y (fy record)
        :xs (feature-matrix record features)
       }
   )
)

(defn ex-5-26
  []
  (let [data (iota/seq "data/soi.csv")
        features [:A02300 :A00200 :AGI_STUB :MARS2 :NUMDEP]
        factors (->> (feature-scales features)
                     (t/tesser (chunks data))
                )
       ]
       (->> (soi-data)
            (r/remove  #(zero? (:zipcode %)))
            ;(r/map #(select-keys % features))
            (r/map (scale-features factors))
            (r/map (extract-features :A02300 features))
            (into [])
            (first)
       )
  )
)

(defn matrix-sum
  [nrows ncols]
  (let [zeros-matrix (i/matrix 0 nrows ncols)]
    {:reducer-identity (constantly zeros-matrix)
     :reducer i/plus
     :combiner-identity (constantly zeros-matrix)
     :combiner i/plus
    }
  )
)

(defn ex-5-27
  []
  (let [columns [:A02300 :A00200 :AGI_STUB :MARS2 :NUMDEP]
        data (iota/seq "data/soi.csv")
       ]
       (->> (prepare-data)
            (t/map (extract-features :A02300 columns))
            (t/map :xs)
            (t/fold (matrix-sum (inc (count columns)) 1))
            (t/tesser (chunks data))
       )
  )
)

(defn calculate-error
  [coefs-t]
  (fn [{:keys [y xs]}]
      (let [y-hat (first (i/mmult coefs-t xs))
            error (- y-hat y)]
           (i/mult xs error)
      )
   )
)

(defn ex-5-28
  []
  (let [columns [:A02300 :A00200 :AGI_STUB :MARS2 :NUMDEP]
        fcount (inc (count columns))
        coefs (vec (repeat fcount 0))
        data (iota/seq "data/soi.csv")
       ]
       (->> (prepare-data)
            (t/map  (extract-features :A02300 columns))
            (t/map  (calculate-error (i/trans coefs)))
            (t/fold (matrix-sum fcount 1))
            (t/tesser (chunks data))
       )
  )
)


(defn ex-5-29
  []
  (let [columns [:A00200 :AGI_STUB :MARS2 :NUMDEP]
        fcount (inc (count columns))
        coefs (vec (repeat fcount 0))
        data (iota/seq "data/soi.csv")
       ]
       (->> (prepare-data)
            (t/map  (extract-features :A02300 columns))
            (t/map  (calculate-error (i/trans coefs)))
            (t/fuse {:sum (t/fold (matrix-sum fcount 1))
                     :count (t/count)}
            )
            (t/post-combine (fn [{:keys [sum count]}]
                                 (i/div sum count)
                            )
            )
            (t/tesser (chunks data))
       )
  )
)

(defn matrix-mean
  [nrows ncols]
  (let [zeros-matrix (i/matrix 0 nrows ncols)]
    {:reducer-identity (constantly {:sum zeros-matrix
                                    :count 0})
     :reducer (fn [{:keys [sum count]} x]
                  {:sum (i/plus sum x) :count (inc count)}
              )
     :combiner-identity (constantly {:sum zeros-matrix
                                     :count 0})
     :combiner (fn [a b]
                  (merge-with i/plus a b)
               )
     :post-combiner (fn [{:keys [sum count]}]
                  (i/div sum count)
              )
    }
  )
)
  

(defn ex-5-30
  []
  (let [columns [:A00200 :AGI_STUB :MARS2 :NUMDEP]
        fcount (inc (count columns))
        coefs (vec (repeat fcount 0))
        data (iota/seq "data/soi.csv")
       ]
       (->> (prepare-data)
            (t/map  (extract-features :A02300 columns))
            (t/map  (calculate-error (i/trans coefs)))
            (t/fold (matrix-mean fcount 1))
            (t/tesser (chunks data))
       )
  )
)

(defn update-coefficients 
   [coefs alpha]
   (fn [cost]
       (->> (i/mult cost alpha)
            (i/minus coefs)
       )
   )
)

(defn gradient-descent-fold
  [{:keys [fy features factors 
               coefs alpha]}]
  (let [zeros-matrix (i/matrix 0 (count features) 1)]
       (->> (prepare-data)
            (t/map  (scale-features factors))
            (t/map  (extract-features fy features))
            (t/map  (calculate-error (i/trans coefs)))
            (t/fold (matrix-mean (inc (count features)) 1))
            (t/post-combine (update-coefficients coefs alpha))
       )
  )
)

(defn ex-5-31
  []
  (let [features [:A00200 :AGI_STUB :MARS2 :NUMDEP]
        fcount (inc (count features))
        coefs (vec (repeat fcount 0))
        data (iota/seq "data/soi.csv")
        factors (->> (feature-scales features)
                     (t/tesser (chunks data))
                )
        options {:fy :A02300 :features features :factors factors
                 :coefs coefs :alpha 0.1}
       ]
       (->> (gradient-descent-fold options)
            (t/tesser (chunks data))
       )
  )
)


(defn descend
   [options data]
   (fn [coefs] 
       (->> (gradient-descent-fold (assoc options :coefs coefs))
            (t/tesser (chunks data))
       )
   )
)

(defn ex-5-32
  []
  (let [features [:A00200 :AGI_STUB :MARS2 :NUMDEP]
        fcount (inc (count features))
        coefs (vec (repeat fcount 0))
        data (iota/seq "data/soi-sample.csv")
        factors (->> (feature-scales features)
                     (t/tesser (chunks data))
                )
        options {:fy :A02300 :features features :factors factors
                 :coefs coefs :alpha 0.1}
        iterations 100
        xs (range iterations)
        ys (->> (iterate (descend options data) coefs)
                (take iterations)
           )
       ]
       (println (into [](map second ys)))
       (-> (c/xy-plot xs (map first ys)
                      :x-label "Iterations"
                      :y-label "Coeff"
           )
           (c/add-lines xs (map second ys))
           (c/add-lines xs (map #(nth % 2) ys))
           (c/add-lines xs (map #(nth % 3) ys))
           (c/add-lines xs (map #(nth % 4) ys))
           (i/view)
       )
  )
)



(defn ex-5-33
  []
  (->> (text/dseq "data/soi.csv")
       (r/take 2)
       (into [])
  )
)


(defn ex-5-34
  []
  (let [conf (conf/ig)
        input (text/dseq "data/soi.csv")
        workdir (rand-file "tmp")
        features [:A00200 :AGI_STUB :MARS2 :NUMDEP]
       ]
     (h/fold conf input workdir #'feature-scales features)
  )
)

(defn hadoop-gradient-descent 
   [conf input-file workdir]
   (let [features [:A00200 :AGI_STUB :MARS2 :NUMDEP]
         fcount (inc (count features))
         coefs (vec (repeat fcount 0))
         input (text/dseq input-file)
         options {:features features 
                  :dy :A02300 
                  :coefs coefs 
                  :alpha 0.1}
         
         factors (h/fold conf input (rand-file workdir)
                         #'feature-scales
                         features
                 )
         descend (fn [coefs] 
                   (h/fold conf input (rand-file workdir)
                           #'gradient-descent-fold
                           (merge options {:coefs coefs
                                           :factors factors}
                           )
                   )
                  )
        ]
       (take 5 (iterate descend coefs))
   )
)


;(defn ex-5-35
;  []
;  (let [conf (conf/ig)
;        workdir "tmp"
;       ]
;     (hadoop-gradient-descent  conf "data/soi.csv" workdir)
;  )
;)
   


(defn stohastic-gradient-descent
   [options data]
   (let [batches (->> (into [] data)
                      (shuffle)
                      (partition 250)
                 )
        descend (fn [coefs batch]
                    (->> (gradient-descent-fold (assoc options :coefs coefs))
                         (t/tesser (chunks batch))
                    )
                )
        ]
        (reductions  descend (:coefs options) batches)
   )
)

(defn ex-5-36
  []
  (let [features [:A00200 :AGI_STUB :MARS2 :NUMDEP]
        fcount (inc (count features))
        coefs (vec (repeat fcount 0))
        data (iota/seq "data/soi.csv")
        factors (->> (feature-scales features)
                     (t/tesser (chunks data))
                )
        options {:fy :A02300 :features features :factors factors
                 :coefs coefs :alpha 0.001}
        ys (stohastic-gradient-descent options data)
        xs (range (count ys))
       ]
       (println (into [](map second ys)))
       (-> (c/xy-plot xs (map first ys)
                      :x-label "Iterations"
                      :y-label "Coeff"
           )
           (c/add-lines xs (map second ys))
           (c/add-lines xs (map #(nth % 2) ys))
           (c/add-lines xs (map #(nth % 3) ys))
           (c/add-lines xs (map #(nth % 4) ys))
           (i/view)
       )
  )
)



