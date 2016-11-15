
(ns latte-integers.arith
  "The arithmetic functions over ℤ."

  (:refer-clojure :exclude [and or not int])

  (:require [latte.core :as latte :refer [defaxiom defthm definition
                                          deflemma
                                          lambda forall proof assume have
                                          try-proof ==>]]

            [latte.prop :as p :refer [and or not <=>]]
            [latte.equal :as eq :refer [equal]]
            [latte.quant :as q]
            [latte.classic :as classic]
            [latte.fun :as fun]

            [latte-sets.core :as set :refer [elem forall-in]]

            [latte-integers.core :as int :refer [zero succ pred int]]
            [latte-integers.nat :as nat :refer [positive negative]]))

(defaxiom int-recur
  "The recursion principle for integers.

According to [TT&FP,p. 318], this is derivable,
 but we introduce it as an axiom since the
derivation seems rather complex."
  [[T :type] [x T] [f-succ (==> T T)] [f-pred (==> T T)]]
  (q/unique
   (==> int T)
   (lambda [g (==> int T)]
     (and (equal T (g zero) x)
          (forall [y int]
            (and (==> (positive (succ y))
                      (equal T (g (succ y)) (f-succ (g y))))
                 (==> (negative (pred  y))
                      (equal T (g (pred y)) (f-pred (g y))))))))))

(defthm int-recur-bijection
  "The recursion principle for integers, for bijections."
  [[T :type] [x T] [f (==> T T)] [b (fun/bijective T T f)]]
  (q/unique
   (==> int T)
   (lambda [g (==> int T)]
     (and (equal T (g zero) x)
          (forall [y int]
            (equal T (g (succ y)) (f (g y))))))))


(deflemma int-recur-bijection-lemma-1
  [[T :type] [f (==> T T)] [b (fun/bijective T T f)] [g (==> int T)]]
  (==> (forall [y int]
         (and (==> (positive (succ y))
                   (equal T (g (succ y)) (f (g y))))
              (==> (negative (pred y))
                   (equal T (g (pred y)) ((fun/inverse T T f b) (g y))))))
       (forall [y int]
         (equal T (g (succ y)) (f (g y))))))

(proof int-recur-bijection-lemma-1
    :script
  (have inv-f _ :by (fun/inverse T T f b))
  (assume [H (forall [y int]
                     (and (==> (positive (succ y))
                               (equal T (g (succ y)) (f (g y))))
                          (==> (negative (pred y))
                               (equal T (g (pred y)) (inv-f (g y))))))]
    (assume [y int]
      "We proceed by case analysis."
      "  - first case: y is positive"
      (assume [Hpos (positive y)]
        (have <a1> (positive (succ y)) :by ((nat/positive-succ-strong y) Hpos))
        (have <a> (equal T (g (succ y)) (f (g y)))
              :by ((p/and-elim-left% (H y)) <a1>)))
      "  - second case: y is zero"
      (assume [Hzero (equal int y zero)]
        (have <b1> (positive (succ zero))
              :by ((nat/positive-succ zero)
                   nat/nat-zero))
        (have <b2> (positive (succ y))
              :by ((eq/eq-subst int
                                (lambda [z int] (positive (succ z)))
                                zero y)
                   ((eq/eq-sym int y zero) Hzero)
                   <b1>))
        (have <b> (equal T (g (succ y)) (f (g y)))
              :by ((p/and-elim-left% (H y)) <b2>)))
      "we regroup the first two cases"
      (assume [Hnat (or (equal int y zero)
                        (positive y))]
        (have <c> (equal T (g (succ y)) (f (g y)))
              :by ((p/or-elim (equal int y zero)
                              (positive y))
                   Hnat
                   (equal T (g (succ y)) (f (g y)))
                   <b> <a>)))
      "  - third case: y is negative"
      (assume [Hneg (negative y)]
        (have <d1> (negative (pred (succ y)))
              :by ((eq/eq-subst int (lambda [z int] (negative z)) y (pred (succ y)))
                   ((eq/eq-sym int (pred (succ y)) y) (int/pred-of-succ y))
                   Hneg))
        (have <d2> (equal T (g (pred (succ y))) (inv-f (g (succ y))))
              :by ((p/and-elim-right% (H (succ y))) <d1>))
        (have <d3> (equal T (g y) (inv-f (g (succ y))))
              :by ((eq/eq-subst int (lambda [z int] (equal T (g z) (inv-f (g (succ y))))) (pred (succ y)) y)
                   (int/pred-of-succ y)
                   <d2>))
        (have <d4> (equal T (f (g y)) (f (inv-f (g (succ y)))))
              :by ((eq/eq-cong T T f (g y) (inv-f (g (succ y))))
                   <d3>))
        (have <d5> (equal T (f (inv-f (g (succ y)))) (g (succ y)))
              :by ((fun/inverse-prop T T f b)
                   (g (succ y))))
        (have <d> (equal T (g (succ y)) (f (g y)))
              :by ((eq/eq-sym T (f (g y)) (g (succ y)))
                   ((eq/eq-trans T (f (g y)) (f (inv-f (g (succ y)))) (g (succ y)))
                    <d4> <d5>))))
      "We regroup the cases (or elimination)"
      (have <e> (equal T (g (succ y)) (f (g y)))
            :by ((p/or-elim (or (equal int y zero)
                                (positive y))
                            (negative y))
                 (nat/int-split y)
                 (equal T (g (succ y)) (f (g y)))
                 <c> <d>))
      (qed <e>))))

(deflemma int-recur-bijection-lemma-2
  [[T :type] [f (==> T T)] [b (fun/bijective T T f)] [g (==> int T)]]
  (==> (forall [y int]
         (equal T (g (succ y)) (f (g y))))
       (forall [y int]
         (and (==> (positive (succ y))
                   (equal T (g (succ y)) (f (g y))))
              (==> (negative (pred y))
                   (equal T (g (pred y)) ((fun/inverse T T f b) (g y))))))))

(proof int-recur-bijection-lemma-2
    :script
  (have inv-f _ :by (fun/inverse T T f b))
  (assume [H (forall [y int]
               (equal T (g (succ y)) (f (g y))))]
    (assume [y int]
      (assume [Hpos (positive (succ y))]
        (have <a> (equal T (g (succ y)) (f (g y))) :by (H y)))
      (assume [Hneg (negative (pred y))]
        (have <b1> (equal T (g (succ (pred y))) (f (g (pred y))))
              :by (H (pred y)))
        (have <b2> (equal T (g y) (f (g (pred y))))
              :by ((eq/eq-subst int
                                (lambda [z int] (equal T (g z) (f (g (pred y)))))
                                (succ (pred y)) y)
                   (int/succ-of-pred y)
                   <b1>))
        (have <b3> (equal T (f (g (pred y))) (g y))
              :by ((eq/eq-sym T (g y) (f (g (pred y)))) <b2>))
        (have <b4> (equal T (inv-f (f (g (pred y)))) (inv-f (g y)))
              :by ((eq/eq-cong T T inv-f (f (g (pred y))) (g y))
                   <b3>))
        (have <b5> (equal T (inv-f (f (g (pred y)))) (g (pred y)))
              :by ((fun/inverse-prop-conv T T f b) (g (pred y))))
        (have <b6> (equal T (g (pred y)) (inv-f (f (g (pred y)))))
              :by ((eq/eq-sym T (inv-f (f (g (pred y)))) (g (pred y))) <b5>))
        (have <b> (equal T (g (pred y)) (inv-f (g y)))
              :by ((eq/eq-trans T (g (pred y)) (inv-f (f (g (pred y)))) (inv-f (g y)))
                   <b6> <b4>)))
      "regroup the two conjuncts."
      (have <c> _ :by (p/and-intro% <a> <b>))
      (qed <c>))))

(deflemma int-recur-bijection-lemma
  [[T :type] [f (==> T T)] [b (fun/bijective T T f)] [g (==> int T)]]
  (<=> (forall [y int]
         (and (==> (positive (succ y))
                   (equal T (g (succ y)) (f (g y))))
              (==> (negative (pred y))
                   (equal T (g (pred y)) ((fun/inverse T T f b) (g y))))))
       (forall [y int]
         (equal T (g (succ y)) (f (g y))))))

(proof int-recur-bijection-lemma
    :term
  ((p/iff-intro 
       (forall [y int]
         (and (==> (positive (succ y))
                   (equal T (g (succ y)) (f (g y))))
              (==> (negative (pred y))
                   (equal T (g (pred y)) ((fun/inverse T T f b) (g y))))))
     (forall [y int]
       (equal T (g (succ y)) (f (g y)))))
   (int-recur-bijection-lemma-1 T f b g)
   (int-recur-bijection-lemma-2 T f b g)))

