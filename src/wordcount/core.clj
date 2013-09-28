(ns wordcount.core
  (:require [wordcount.pages :refer :all]
            [wordcount.words :refer :all]
            [clojure.core.reducers :as r]
            [foldable-seq.core :refer [foldable-seq]]))

(defn frequencies-fold [words]
  (r/fold (partial merge-with +)
          (fn [counts word] (assoc counts word (inc (get counts word 0))))
          words))

(defn frequencies-pmap [words partition-size]
  (reduce (partial merge-with +) 
    (pmap frequencies 
      (partition-all partition-size words))))

(defn frequencies-partition-then-fold [words partition-size]
  (reduce (partial merge-with +)
    (map #(frequencies-fold (into [] %))
      (partition-all partition-size words))))

(defn count-words-sequential [pages]
  (frequencies (mapcat get-words pages)))

(defn count-words-fold [pages]
  (frequencies-fold (r/mapcat get-words (foldable-seq pages))))

(defn count-words-pmap [pages partition-size]
  (frequencies-pmap (mapcat get-words pages) partition-size))

(defn count-words-partition-then-fold [pages partition-size]
  (frequencies-partition-then-fold (mapcat get-words pages) partition-size))

(defn -main [& args]
  (let [[page-count filename algorithm psize] args
        pages (get-pages (Integer. page-count) filename)
        partition-size (Integer. psize)]
    (time
      (case algorithm
        "sequential" (count-words-sequential pages)
        "fold" (count-words-fold pages)
        "pmap" (count-words-pmap pages partition-size)
        "pthenf" (count-words-partition-then-fold pages partition-size))))
  nil)