(ns chapter5.examples
  (:require [chapter5.data :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.core.reducers :as r]
            [iota]
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



