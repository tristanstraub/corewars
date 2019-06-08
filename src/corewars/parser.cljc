(ns corewars.parser
  (:require [instaparse.core :as insta]))

(def parser
  (insta/parser "<S>           = instructions;
<instructions> = instruction | (instruction <ws>+ instructions*)
ws          = #'[ \n\t]';
<number>      = #'[-]?[0-9]+';
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

(defn parse
  [input]
  (insta/parse parser input))
