(ns chapter4.data
  (:require [clojure.java.io :as io]
            [incanter.core :as i]
            [incanter.io :as iio]
            [incanter.excel :as xls])
)



(defn titanic-data
  []
  (-> (io/resource "titanic.tsv")
      (str)
      (iio/read-dataset :delim \tab :header true)
  )
)


