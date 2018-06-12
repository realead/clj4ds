(ns chapter1.core
  (:require [chapter1.data :refer :all]
            [chapter1.examples :refer :all]
            [clojure.java.io :as io]
            [incanter.core :as i]
            [incanter.excel :as xls])
  (:gen-class))



(def examples [
                   ex-1-1 
                   ex-1-3 
                   ex-1-6 
                   ex-1-7
                   ex-1-8
                   ex-1-9
                   ex-1-10
              ]
)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (doseq  [f examples] 
      (-> (f)
          (pr-str)
          (println)
      )
  )
)
