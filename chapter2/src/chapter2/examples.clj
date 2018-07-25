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

