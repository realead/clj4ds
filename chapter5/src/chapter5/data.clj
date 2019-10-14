(ns chapter5.data
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.core.reducers :as r]
            [tesser.core :as t]
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


(def column-names   [:STATEFIPS :STATE :zipcode :AGI_STUB :N1 :MARS1 :MARS2 :MARS4 :PREP :N2 :NUMDEP :A00100 :N00200 :A00200 :N00300 :A00300 :N00600 :A00600 :N00650 :A00650 :N00900 :A00900 :SCHF :N01000 :A01000 :N01400 :A01400 :N01700 :A01700 :N02300 :A02300 :N02500 :A02500 :N03300 :A03300 :N00101 :A00101 :N04470 :A04470 :N18425 :A18425 :N18450 :A18450 :N18500 :A18500 :N18300 :A18300 :N19300 :A19300 :N19700 :A19700 :N04800 :A04800 :N07100 :A07100 :N07220 :A07220 :N07180 :A07180 :N07260 :A07260 :N59660 :A59660 :N59720 :A59720 :N11070 :A11070 :N09600 :A09600 :N06500 :A06500 :N10300 :A10300 :N11901 :A11901 :N11902 :A11902])


(defn format-record [column-names line]
  (zipmap column-names line))

(defn prepare-data 
  []
  (->> (t/remove #(.startsWith % "STATEFIPS"))
       (t/map parse-line)
       (t/map (partial format-record column-names))
       (t/remove #(zero? (:zipcode %)))
  )
)


