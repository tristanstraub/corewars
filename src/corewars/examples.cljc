(ns corewars.examples
  (:require [corewars.parser :as parser]
            [corewars.emitter :as emitter]))

(def dwarf
  "DAT 0
  ADD #4 -1
  MOV #3 @-2
  JMP -2")

(def imp
  "MOV 0 1")

(def imp-bin (emitter/assemble (parser/parse imp)))
(def dwarf-bin (emitter/assemble (parser/parse dwarf)))

(def machine
  {:memory (vec (first (partition 4096 4096 (repeat 0)
                                  (concat imp-bin
                                          (repeat 1000 0)
                                          dwarf-bin))))
   :ptr    0
   :ptrs   [0 (+ (count imp-bin) 1000)]})
