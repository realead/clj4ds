(ns chapter5.examples
  (:require [chapter5.data :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.core.reducers :as r]
            [incanter.core :as i]
            [iota]            
            [tesser.math :as m]
            [tesser.core :as t]
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

      


