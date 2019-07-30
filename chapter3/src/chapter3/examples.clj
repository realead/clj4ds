(ns chapter3.examples
  (:require [chapter3.data :refer :all]
            [clojure.java.io :as io]
            [incanter.core :as i]
            [incanter.excel :as xls]
            [incanter.stats :as s]
            [incanter.charts :as c]
            [incanter.distributions :as d]
            [clj-time.format :as f]
            [clj-time.predicates :as p]
  )
)

(defn ex-3-1
  [data]
  (i/view data)
)


(defn ex-3-2
  [data]
  (-> (remove nil? (i/$ "Height, cm" data))
      (c/histogram :nbins 20
                   :x-label "height, cm"
                   :y-label "Frequency")
      (i/view)
  )
)


(defn ex-3-3
  [data]
  (-> (remove nil? (i/$ "Weight" data))
      (c/histogram :nbins 20
                   :x-label "weight, cm"
                   :y-label "Frequency")
      (i/view)
  )
)


(defn ex-3-4
  [data]
  (->>  (i/$ "Weight" data)
        (remove nil?)
        (s/skewness)
        (println "weight skewness")
  )
  (->>  (i/$ "Weight" data)
        (remove nil?)
        (i/log)
        (s/skewness)
        (println "log weight skewness")
  )
  (->>  (i/$ "Height, cm" data)
        (remove nil?)
        (s/skewness)
        (println "height skewness")
  )
)


(defn ex-3-5
  [data]
  (-> (remove nil? (i/$ "Weight" data))
      (i/log)
      (c/histogram :nbins 20
                   :x-label "weight, cm"
                   :y-label "Frequency")
      (i/view)
  )
)

(defn swimmer-data
   [data]
   (i/$where {"Height, cm" {:$ne nil} "Weight" {:$ne nil} "Sport" {:$eq "Swimming"}} data)
)


(defn ex-3-6
  [data]
  (let [swimmers (swimmer-data data)
        heights (i/$ "Height, cm" swimmers)
        weights (i/log (i/$ "Weight" swimmers))]
      (-> (c/scatter-plot heights weights
                        :x-label "Height, cm"
                        :y-label "Weight")
          (i/view)
      )
  )
)


(defn jitter 
  [limit]
  (fn [x]
     (let [amount (- (rand (* 2 limit)) limit)]
        (+ x amount)
     )
  )
)

(defn ex-3-7
  [data]
  (let [swimmers (swimmer-data data)
        heights (->> (i/$ "Height, cm" swimmers)
                     (map (jitter 0.5))
                )
        weights (->> (i/$ "Weight" swimmers)
                     (map (jitter 0.5))
                     (i/log)
                )]
      (-> (c/scatter-plot heights weights
                        :x-label "Height, cm"
                        :y-label "Weight")
          (i/view)
      )
  )
)

