(ns corewars.parse
  (:require [instaparse.core :as insta]))

(defmulti machine-eval (fn [machine [mnemonic & ops]] mnemonic))

(defn machine-next
  [machine]
  (update machine :ptr (fn [ptr] (mod (inc ptr) (count (:memory machine))))))

(defn machine-store
  [machine value addr]
  (assoc-in machine [:memory addr] value))

(defn machine-get
  [machine addr]
  (get-in machine [:memory addr]))

(defmethod machine-eval :dat
  [machine [_ & ops]]
  (machine-next machine))

(defn machine-addr
  [machine [addr-type offset]]
  (let [base (+ (:ptr machine) (Integer/parseInt offset))]
    (case addr-type
      :relative base
      :indirect (+ base (machine-get machine base)))))

(defn machine-load
  [machine [value-type value :as op]]
  (case value-type
    :immediate (Integer/parseInt (str value))
    (:relative :indirect) (machine-get machine (machine-addr machine op))))

(defmethod machine-eval :add
  [machine [_ op1 op2]]
  (-> machine
      (machine-store (+ (machine-load machine op1)
                      (machine-load machine op2))
                   (machine-addr machine op2))
      (machine-next)))

(defmethod machine-eval :mov
  [machine [_ op1 op2]]
  (-> machine
      (machine-store (machine-load machine op1)
                     (machine-addr machine op2))
      (machine-next)))

(defmethod machine-eval :jmp
  [machine [_ op]]
  (assoc machine :ptr (machine-addr machine op)))

(defn machine-step
  [machine]
  (machine-eval machine (get-in machine [:instructions (:ptr machine)])))

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

(def dwarf
  "DAT 0
  ADD #4 -1
  MOV #3 @-2
  JMP -2")

(def machine
  {:memory       (vec (repeat 8000 0))
   :instructions (parse dwarf)
   :ptr          0})
