(ns chapter2.data
  (:require [clojure.java.io :as io]
            [incanter.io :as iio]))



(defn load-data
  [file]
  (-> (io/resource file)
      (iio/read-dataset :header true :delim \tab)
  )
)

