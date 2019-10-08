(ns chapter5.data
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.core.reducers :as r]
            [iota])
)



(defn parse-double 
  [x]
  (Double/parseDouble x)
)
  
(defn parse-header
  [line]
  (->> (str/split line #",")
       (map keyword)
  )
)


(defn parse-line
  [line]
  (let [[text-fields double-fields] (->> (str/split line #",")
                                      (split-at 2)
                                    )
       ]
    (concat text-fields 
            (map parse-double double-fields)
    )
   )
)

(defn soi-data
  []
  (let [data (iota/seq "data/soi.csv")
        header (parse-header (first data))
       ]
     (->> (r/drop 1 data)
          (r/map parse-line)
          (r/map (fn [fields]
                     (zipmap header fields)
                 )
          )
     )
  )
)


