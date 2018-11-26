
(ns latte-integers.main
  (:gen-class)
  (:require [latte.main :refer [latte-main]]
            [latte-integers.core]
            [latte-integers.nat]
            [latte-integers.rec]
            [latte-integers.plus]
            [latte-integers.minus]
            [latte-integers.ord]
            [latte-integers.times]
            [latte-integers.divides]))

(defn -main [& args]
  (latte-main args "latte-integers"
              '[latte-integers.core
                latte-integers.nat
                latte-integers.rec
                latte-integers.plus
                latte-integers.minus
                latte-integers.ord
                latte-integers.times
                latte-integers.divides]))

