(ns chapter2.examples
  (:require [chapter2.data :refer :all]
            [clojure.java.io :as io]
            [incanter.core :as i]
            [incanter.excel :as xls]
            [incanter.stats :as s]
            [incanter.charts :as c]
            [incanter.distributions :as d]
            [clj-time.format :as f])
)

(defn ex-2-1
  []
  (-> (load-data "dwell-times.tsv")
      (i/view)
  )
)



(defn ex-2-2
  []
  (-> (i/$ :dwell-time (load-data "dwell-times.tsv"))
      (c/histogram :x-label "Dwell time (s)" :nbins 50)
      (i/view)
  )
)


(defn ex-2-3
  []
  (-> (i/$ :dwell-time (load-data "dwell-times.tsv"))
      (c/histogram :x-label "Dwell time (s)" :nbins 50)
      (c/set-axis :y (c/log-axis :label "Log Frequency"))
      (i/view)
  )
)


(defn ex-2-4
  []
  (let  [data (i/$ :dwell-time (load-data "dwell-times.tsv"))]
      (println "Mean:" (s/mean data))
      (println "Median:" (s/median data))
      (println "SD:" (s/sd data))
  )
)


(defn ex-2-5
  []
  (let  [data (->> (load-data "dwell-times.tsv")
                   (daily-mean-dwell-times)
                   (i/$ :dwell-time)
              )]
      (println "Mean:" (s/mean data))
      (println "Median:" (s/median data))
      (println "SD:" (s/sd data))
  )
)



(defn ex-2-6
  []
  (let  [data (->> (load-data "dwell-times.tsv")
                   (daily-mean-dwell-times)
                   (i/$ :dwell-time)
              )]
      (-> (c/histogram data 
                   :x-label "Daily mean dwelltime"
                   :nbins 20)
           (i/view)
      )
  )
)

(defn ex-2-7
  []
  (let  [data (->> (load-data "dwell-times.tsv")
                   (daily-mean-dwell-times)
                   (i/$ :dwell-time)
              )
         mean (s/mean data)
         sd   (s/sd data)
         pdf  (fn [x] (s/pdf-normal x :mean mean :sd sd))]
      (-> (c/histogram data 
                   :x-label "Daily mean dwelltime"
                   :nbins 20
                   :density true)
           (c/add-function pdf 80 100)
           (i/view)
      )
  )
)

(defn standard-error
  [data]
  (/ (s/sd data) (Math/sqrt (count data)))
)

(defn ex-2-8
  [data]
  (let  [may-1 (f/parse-local-date "2015-05-01")]
      (->> (with-parsed-date data)
           (filter-days-dwell-times may-1)
           (standard-error)
      )
  )
)


(defn confidence-interval 
  [p xs]
  (let [x-bar (s/mean xs)
        se (standard-error xs)
        z-crit (s/quantile-normal (- 1 (/ (- 1 p) 2)))]
     [(- x-bar (* se z-crit))
      (+ x-bar (* se z-crit))]
   )
)

(defn ex-2-9
  [data]
  (let  [may-1 (f/parse-local-date "2015-05-01")]
      (->> (with-parsed-date data)
           (filter-days-dwell-times may-1)
           (confidence-interval 0.95)
      )
  )
)



