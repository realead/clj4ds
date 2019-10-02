(ns chapter4.examples
  (:require [chapter4.data :refer :all]
            [clojure.java.io :as io]
            [incanter.core :as i]
            [incanter.excel :as xls]
            [incanter.stats :as s]
            [incanter.charts :as c]
            [incanter.distributions :as d]
            [incanter.optimize :as o]
            [clj-time.core :as time]
            [clj-time.format :as f]
            [clj-time.predicates :as p]
            [clj-time.coerce :as coerce]
  )
)


(defn ex-4-1
  [data]
  (i/view data)
)


(defn frequency-table 
  [sum-column-name group-columns dataset]
  (->> (i/$ group-columns dataset)
       (i/add-column sum-column-name (repeat 1))
       (i/$rollup :count sum-column-name group-columns)
  )
)

(defn ex-4-2
  [data]
  (frequency-table :count [:sex :survived] data)
)

(defn frequency-map
  [sum-column-name group-columns dataset]
  (let [f (fn [freq-map row]
              (let [groups (map row group-columns)
                    value  (get row sum-column-name)]
                    (assoc-in freq-map groups value)
               )
           )
        ]
        (->> (frequency-table sum-column-name group-columns dataset)
             (:rows)
             (reduce f {})
        )
  )
)

(defn ex-4-3
  [data]
  (frequency-map :count [:sex :survived] data)
)


(defn fatalaties-by-sex
  [data]
  (let [totals (frequency-map :count [:sex] data)
        groups (frequency-map :count [:sex :survived] data)]
        {:male (/ (get-in groups ["male" "n"])
                  (get totals "male")
               )
         :female (/ (get-in groups ["female" "n"])
                  (get totals "female")
               )
        }
   )
)


(defn ex-4-4
  [data]
  (fatalaties-by-sex data)
)


(defn relative-risk
   [p1 p2]
   (float (/ p1 p2))
)

(defn ex-4-5
  [data]
  (let [proportions (fatalaties-by-sex data)]
      (relative-risk (get proportions :male)
                     (get proportions :female)
      )
  )
)


(defn odd-ratio
   [p1 p2]
   (float 
      (/ (* p1 (- 1 p2))
         (* p2 (- 1 p1))
      )
   )
)


(defn ex-4-6
  [data]
  (let [proportions (fatalaties-by-sex data)]
      (odd-ratio     (get proportions :male)
                     (get proportions :female)
      )
  )
)


(defn ex-4-7
  [data]
  (let [passengers (concat (repeat 127 0)
                           (repeat 339 1)
                   )
        bootstrap (s/bootstrap passengers i/sum :size 10000)]
        (-> (c/histogram bootstrap
                         :x-label "Female survivors"
                         :nbins 20
            )
            (i/view)
        )
   )
)

(defn ex-4-8
  [data]
  (let [passengers (concat (repeat 127 0)
                           (repeat 339 1)
                   )
        bootstrap (s/bootstrap passengers i/sum :size 10000)]
        (s/sd bootstrap)
   )
)

(defn ex-4-9 
  [data]
  (let [passengers (concat (repeat 127 0)
                           (repeat 339 1)
                   )
        bootstrap (s/bootstrap passengers i/sum :size 10000)
        binom  (fn [x] (s/pdf-binomial x :size 466 :prob (/ 339 466)))
        normal (fn [x] (s/pdf-normal x :mean 339 :sd 9.57))
       ]
        (-> (c/histogram bootstrap
                         :x-label "Female survivors"
                         :nbins 20
                         :density true
                         :legend true
            )
            (c/add-function binom 300 380 :series-label "binomial")
            (c/add-function normal 300 380 :series-label "norma")
            (i/view)
        )
   )
)

(defn se-binom
   [p n]
   (i/sqrt (* p (- 1 p) (/ 1 n)))
)

(defn ex-4-10
  [data]
  (let [groups (frequency-map :count [:sex :survived] data)
        survived (get-in groups ["female" "y"])
        perished (get-in groups ["female" "n"])
        n (+ survived perished)
        p (float (/ survived n))
      ]
      ;(print groups survived perished)
      (se-binom p n) 
  )
)


(defn ex-4-11
   [data]
   (let [survived (frequency-map :count [:survived] data)
         proportions (fatalaties-by-sex data)
         total (reduce + (vals survived))
         pooled (/ (get survived "n") total)
         se-pooled (se-binom pooled total)
         p-diff (- (get proportions :male)
                   (get proportions :female)
                )
        z-stat (/ p-diff se-pooled)]
      (- 1 (s/cdf-normal (i/abs z-stat)))
   )
)


(defn se-large_proportion
   [p n N]
   (* (se-binom p n)
      (i/sqrt (/ (- N n)
                 (- N 1)
              )
      )
   )
) 


(defn ex-4-12
  [data]
  (frequency-table :count [:pclass :survived] data)
)


(defn ex-4-13
  [data]
  (let [table (frequency-table :count [:pclass :survived] data)]
    (-> (c/stacked-bar-chart :pclass :count
                            :group-by :survived
                            :legend true
                            :x-label "Class"        
                            :y-label "Passengers"
                            :data table
        )
        (i/view)
     )
  )
)

(defn expected-frequencies 
  [data]
  (let [as (vals (frequency-map :count [:survived] data))
        bs (vals (frequency-map :count [:pclass] data))
        total (count (:rows data))]
       (for [a as
             b bs]
             (* a (float (/ b total)))
       )
  )
) 

(defn ex-4-14
  [data]
  (expected-frequencies data)
)


(defn observed-frequencies
  [data]
  (let [as (frequency-map :count [:survived] data)
        bs (frequency-map :count [:pclass] data)
        actual (frequency-map :count [:survived :pclass] data)]
        (for [a (keys as)
              b (keys bs)]
              (get-in actual [a b])
        )
   )
)

(defn ex-4-15
  [data]
  (observed-frequencies data)
)

(defn chisq-stat
  [observed expected]
  (let [f (fn [o e] (/ (i/sq (- o e)) e))]
    (reduce + (map f observed expected))
  )
)


(defn ex-4-16
  [data]
  (let [observed (observed-frequencies data)
        expected (expected-frequencies data)]
     (chisq-stat observed expected)
  )
)

(defn ex-4-17
  [data]
  (let [observed (observed-frequencies data)
        expected (expected-frequencies data)
        x2-stat (chisq-stat observed expected)]
       (s/cdf-chisq x2-stat :df 2 :lower-tail? false)
  )
)


(defn ex-4-18
  [data]
  (let [table (->> (frequency-table :count [:survived :pclass] data)
                   (i/$order [:survived :pclass] :asc)    
              )
        freqs   (i/$ :count table)
        matrix  (i/matrix freqs 3)
       ]
      (println "observed:" table)
      (println "freqs:" freqs)
      (println "observations" matrix)
      (println "chisq-test:")
      (s/chisq-test :table matrix)
  )
)

(defn sigmoid-function
  [coefs]
  (let [z (fn [x] (- (first (i/mmult (i/trans coefs) x))))]
     (fn [x] 
       (/ 1
          (+ 1 (i/exp (z x)))
       )
     )
  )
)

(defn logistic-cost 
  [ys y-hats]
  (let [cost (fn [y y-hat]
                 (if (zero? y)
                     (- (i/log (- 1 y-hat)))
                     (- (i/log y-hat))
                 )
              )
       ]
       (s/mean (map cost ys y-hats))
  )
)


(defn ex-4-19
  []
  (let [f (fn [[x]]
               (i/sq x)
          )
        init [10]]
   (o/minimize f init)
  )
)

(defn ex-4-20
  []
  (let [f (fn [[x]]
              (i/sin x)
          )
       ]
     (println (:value (o/minimize f [1])))
     (println (:value (o/minimize f [10])))
     (println (:value (o/minimize f [100])))
  )
)


(defn logistic-regression 
  [ys xs]
  (let [cost-fn (fn [coefs]
                    (let [classify (sigmoid-function coefs)
                          y-hats (map (comp classify i/trans) xs)
                         ]
                       (logistic-cost ys y-hats)
                    )
                 )
            init-coefs (repeat (i/ncol xs) 0.0)
        ]
       (o/minimize cost-fn init-coefs)
   )
)

(defn add-dummy 
   [col-name from-col value dataset]
   (i/add-derived-column col-name
                         [from-col]
                         #(if (= % value) 1 0)
                         dataset
   )
)

(defn matrix-dataset
    [data]
   (->> (add-dummy :dummy-survived :survived "y" data)
        (i/add-column :bias (repeat 1.0))
        (add-dummy :dummy-mf :sex "male")
        (add-dummy :dummy-1 :pclass "first")
        (add-dummy :dummy-2 :pclass "second")
        (add-dummy :dummy-3 :pclass "third")
        (i/$ [:dummy-survived :bias :dummy-mf :dummy-1 
              :dummy-2 :dummy-3])
        (i/to-matrix)
   )
)


(defn ex-4-21
  [data]
  (let [matrix (matrix-dataset data)
        ys (i/$ 0 matrix)
        xs (i/$ [:not 0] matrix)
        ]
     (-> (logistic-regression ys xs)
         (get :value)
     )
  )
) 

(defn ex-4-22
   [data]
   (let [matrix (matrix-dataset data)
         ys (i/$ 0 matrix)
         xs (i/$ [:not 0] matrix)
         coefs (-> (logistic-regression ys xs)
                    (get :value)
               )
         classifier (comp  #(Math/round %)
                           (sigmoid-function coefs)
                           i/trans
                    )
         ]
         (println "observed  " (map int (take 10 ys)))
         (println "predicted " (map classifier (take 10 xs)))
   )
)


(defn ex-4-23
   [data]
   (let [matrix (matrix-dataset data)
         ys (i/$ 0 matrix)
         xs (i/$ [:not 0] matrix)
         coefs (-> (logistic-regression ys xs)
                    (get :value)
               )
         classifier (comp  #(Math/round %)
                           (sigmoid-function coefs)
                           i/trans
                    )
         y-hats (map classifier xs)
         ]
         (frequencies (map = y-hats (map int ys)))
   )
)


(defn confusion-matrix
  [ys y-hats]
  (let [classes (into #{} (concat ys y-hats))
        confusion (frequencies (map vector ys y-hats))
        ]
        (i/dataset (cons nil classes)
                   (for [x classes]
                      (cons x
                          (for [y classes]
                               (get confusion [x y])
                           )
                      )
                    )
         )
   )
)

(defn ex-4-24
   [data]
   (let [matrix (matrix-dataset data)
         ys (i/$ 0 matrix)
         xs (i/$ [:not 0] matrix)
         coefs (-> (logistic-regression ys xs)
                    (get :value)
               )
         classifier (comp  #(Math/round %)
                           (sigmoid-function coefs)
                           i/trans
                    )
         y-hats (map classifier xs)
         ]
         (confusion-matrix (map int ys) y-hats)
   )
)


(defn kappa-statistics
  [ys y-hats]
  (let [n (count ys)
        pa (/ (count (filter true? (map = ys y-hats))) n)
        ey (/ (count (filter zero? ys)) n)
        eyh (/ (count (filter zero? y-hats)) n)
        pe (+ (* ey eyh)
              (* (- 1 ey)
                 (- 1 eyh)
               )
            )
        ]
       (float (/ (- pa pe)
                 (- 1 pe)
               )
       )
   )
)

(defn ex-4-25
   [data]
   (let [matrix (matrix-dataset data)
         ys (i/$ 0 matrix)
         xs (i/$ [:not 0] matrix)
         coefs (-> (logistic-regression ys xs)
                    (get :value)
               )
         classifier (comp  #(Math/round %)
                           (sigmoid-function coefs)
                           i/trans
                    )
         y-hats (map classifier xs)
         ]
         (kappa-statistics (map int ys) y-hats)
   )
)

(defn inc-class-total
  [model class]
  (-> (update-in model [:classes class :total] (fnil inc 0))
      (update-in [:total] (fnil inc 0))
  )
)

(defn inc-predictors-count-fn 
  [row class]
  (fn [model attr]
    (let [val (get row attr)]
       (update-in model [:classes class :predictors attr val] (fnil inc 0))
    )
  )
)

(defn assoc-row-fn 
  [class-attr predictors]
  (fn [model row]
      (let [class (get row class-attr)]
           (reduce (inc-predictors-count-fn row class)
                   (inc-class-total model class)
                   predictors
           )
       )
  )
)

(defn bayes-classifier
  [class-attr predictors data]
    (reduce (assoc-row-fn class-attr predictors) {} data)
)


(defn ex-4-26
  [data]
  (->> (:rows data)
       (bayes-classifier :survived [:sex :pclass])
       (clojure.pprint/pprint)
  )
)



(defn posterior-probability
   [model test class-attr]
   (let [observed (get-in model [:classes class-attr])
         prior (/ (:total observed)
                  (:total model)
               )
        ]
        (apply * prior
                 (for [[predictor value] test]
                      (/ (get-in observed [:predictors  predictor value])
                         (:total observed)
                      )
                 )
        )
   )
)

(defn bayes-classify 
   [model test]
   (let [probability (partial posterior-probability model test)
         classes (keys (:classes model))]
        (apply max-key probability classes)
   )
)

(defn ex-4-27
  [data]
  (let [model (->> (:rows data)
                  (bayes-classifier :survived [:sex :pclass])
              )]
       (println "third class male" 
                    (bayes-classify model {:sex "male" :pclass "third"})
       )
       (println "first class female" 
                    (bayes-classify model {:sex "female" :pclass "first"})
       )
  )
)


(defn ex-4-28
  [data]
  (let [model (->> (:rows data)
                  (bayes-classifier :survived [:sex :pclass])
              )
        test (fn [test]
                (= (:survived test)
                   (bayes-classify model (select-keys test [:sex :class]))
                )
              )
        results (frequencies (map test (:rows data)))
       ]
       (/ (get results true)
          (apply + (vals results))
       )
  )
)


(defn ex-4-29
  [data]
  (let [model (->> (:rows data)
                  (bayes-classifier :survived [:sex :pclass])
              )
        classify (fn [test]
                   (->> (select-keys test [:sex :class])
                     (bayes-classify model)
                   )
                 )
        ys (map :survived (:rows data))
        y-hats (map classify (:rows data))
       ]
       (confusion-matrix ys y-hats)
  )
)

(defn information 
  [p]
  (- (i/log2 p))
)

(defn entropy 
   [xs]
   (let [n (count xs)
         f (fn [x] 
               (let [p (/ x n)]
                    (* p (information p))
               )
            )
        ]
        (->> (frequencies xs)
             (vals)
             (map f)
             (reduce +)
        )
    )
)

(defn ex-4-3031
  []
  (let [red-black (concat (repeat 26 1)
                          (repeat 26 0)
                  )
        picture-not-picture (concat (repeat 12 1)
                                    (repeat 40 0)
                            )
       ]
       (println "red-black" (entropy red-black))
       (println "pic-not-pic" (entropy picture-not-picture))
   )
)

(defn ex-4-32
  [data]
  (->> (:rows data)
       (map :survived)
       (entropy)
  )
)

(defn weighted-entropy
   [groups]
   (let [n (count (apply concat groups))
         e (fn [group]
               (* (entropy group)
                  (/ (count group) n)
               )
            )
        ]
        (->> (map e groups)
             (reduce +) 
        )
   )
)

(defn ex-4-33
  [data]
  (->> (:rows data)
       (group-by :sex)
       (vals)
       (map (partial map :survived))
       (weighted-entropy)
  )
)

(defn information-gain
    [groups]
    ( - (entropy (apply concat groups))
        (weighted-entropy groups)
    )
)


(defn ex-4-34
  [data]
  (->> (:rows data)
       (group-by :pclass)
       (vals)
       (map (partial map :survived))
       (information-gain)
  )
)
              


