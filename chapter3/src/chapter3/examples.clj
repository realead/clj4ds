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
  []
  (-> (athlete-data)
      (i/view)
  )
)


