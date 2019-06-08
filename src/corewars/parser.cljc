(ns corewars.parser
  (:require [instaparse.core :as insta]))

(def parser
  (insta/parser "<S>           = instructions;
<instructions> = instruction | (instruction <ws>+ instructions*)
ws          = #'[ \n\t]';
number      = #'[-]?[0-9]+';
<instruction> = <ws>* (dat | add | mov | jmp) <ws>*;
dat         = <'DAT'> <ws>+ number;
add         = <'ADD'> <ws>+ op <ws>+ op;
mov         = <'MOV'> <ws>+ op <ws>+ op;
jmp         = <'JMP'> <ws>+ op;
<op>          = immediate | relative | indirect;
relative    = number;
immediate   = <'#'> number;
indirect    = <'@'> number;
"))

(defn parse-integer
  [v]
  (int #?(:clj (read-string v)
          :cljs (cljs.reader/read-string v))))

(defn parse
  [input]
  (insta/transform {:dat (fn [value]
                           [:dat nil [:immediate value]])
                    :jmp (fn [value]
                           [:jmp nil value])
                    :number (fn [value]
                              (parse-integer value))}
                   (insta/parse parser input)))
