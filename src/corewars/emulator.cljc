(ns corewars.emulator
  (:require [corewars.emitter :as emitter]
            [corewars.parser :as parser]
            [corewars.examples :as examples]
            [corewars.decoder :as decoder]))

(def machine
  {:memory       (vec (first (partition 4096 4096 (repeat 0) (emitter/assemble (parser/parse examples/dwarf)))))
   :ptr          0})
