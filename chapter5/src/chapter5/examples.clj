(ns chapter5.examples
  (:require [chapter5.data :refer :all]
            [clojure.java.io :as io]
            [incanter.core :as i]
            [clojure.string :as str]
            [clojure.core.reducers :as r]
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


