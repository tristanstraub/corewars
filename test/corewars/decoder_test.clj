(ns corewars.decoder-test
  (:require  [clojure.test :refer [deftest is]]
             [corewars.emitter :as emitter]
             [corewars.decoder :as decoder]
             [corewars.parser :as parser]
             [corewars.examples :as examples]))

(deftest assemble-disassemble-test
  (is (= [[:dat nil [:immediate 0]]
          [:add [:immediate 4] [:relative -1]]
          [:mov [:immediate 3] [:indirect -2]]
          [:jmp nil [:relative -2]]]
         (parser/parse examples/dwarf)))
  (is (= (parser/parse examples/dwarf)
         (decoder/disassemble (emitter/assemble (parser/parse examples/dwarf))))))
