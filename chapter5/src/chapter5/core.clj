(ns chapter5.core
  (:require [chapter5.data :refer :all]
            [chapter5.examples :refer :all])
  (:gen-class))



(def examples [
              
              ]
)

(def examples-with-data [
 
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

  (let [data (titanic-data)]
      (doseq  [f examples-with-data] 
          (-> (f data)
              (pr-str)
              (println)
          )
      )
  )
)
