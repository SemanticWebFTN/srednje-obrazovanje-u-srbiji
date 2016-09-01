(ns grasp-jsons.core
  (:gen-class)
  (:require [cheshire.core :refer [parse-string]]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn read-json
  [path]
  (parse-string (slurp path)))
