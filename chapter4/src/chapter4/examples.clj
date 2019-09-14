(ns chapter4.examples
  (:require [chapter4.data :refer :all]
            [clojure.java.io :as io]
            [incanter.core :as i]
            [incanter.excel :as xls]
            [incanter.stats :as s]
            [incanter.charts :as c]
            [incanter.distributions :as d]
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
