(ns wordcount.core
  (:require [wordcount.pages :refer :all]
            [wordcount.words :refer :all]
            [clojure.core.reducers :as r]
            [foldable-seq.core :refer [foldable-seq]]))

(defn frequencies-fold [words]
  (r/fold (partial merge-with +)
          (fn [counts word] (assoc counts word (inc (get counts word 0))))
          words))

(defn frequencies-pmap [words]
  (reduce (partial merge-with +) 
    (pmap frequencies 
      (partition-all 10000 words))))

(defn count-words-sequential [pages]
  (frequencies (mapcat get-words pages)))

(defn count-words-fold [pages]
  (frequencies-fold (r/mapcat get-words (foldable-seq pages))))

(defn count-words-pmap [pages]
  (frequencies-pmap (mapcat get-words pages)))

(defn -main [& args]
  ; (time (count-words-sequential (get-pages 10000 "enwiki.xml")))
  ; (time (count-words-fold (get-pages 10000 "enwiki.xml")))
  (time (count-words-pmap (get-pages 10000 "enwiki.xml")))
  nil)
