(ns chapter1.core
  (:require [chapter1.data :refer :all]
            [chapter1.examples :refer :all]
            [clojure.java.io :as io]
            [incanter.core :as i]
            [incanter.excel :as xls])
  (:gen-class))





(defn -main
  "I don't do a whole lot ... yet."
  [& args]

  (-> (ex-1-1)
      (str)
      (println)
  )

  (-> (ex-1-3)
      (pr-str)
      (println)
  )

  (-> (ex-1-6)
      (pr-str)
      (println)
  )

)
