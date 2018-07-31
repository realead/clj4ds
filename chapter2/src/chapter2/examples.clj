(ns chapter2.examples
  (:require [chapter2.data :refer :all]
            [clojure.java.io :as io]
            [incanter.core :as i]
            [incanter.excel :as xls]
            [incanter.stats :as s]
            [incanter.charts :as c]
            [incanter.distributions :as d])
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

