(ns reaction-grid.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :hsb)
  {:tile-count 10
   :tile-height (/ (q/height) 10)
   :tile-width (/ (q/width) 10)
   :current-shape (q/load-shape "module_7.svg")
   :max-dist (q/sqrt (+ (q/sq (q/width)) (q/sq (q/height))))
   :shape-angle 0
   :fill-mode 0
   :size-mode 0
   :shape-size 50
   :shape-color (q/color 0 130 164)})

(defn get-shape-size [size-mode shape-size pos-x pos-y]
  (case size-mode
    1 shape-size
    2 (-  (* shape-size 1.5) (q/map-range (q/dist (q/mouse-x) (q/mouse-y) pos-x pos-y) 0 500 5 shape-size))
    3 q/map-range (q/dist (q/mouse-x) (q/mouse-y) pos-x pos-y) 0 500 5 shape-size))

(defn luk-and-fill [fill-mode current-shape shape-color pos-x pos-y max-dist]
  (case fill-mode
    0 (.enableStyle current-shape)
    1 (do (.disableStyle current-shape)
          (q/fill shape-color))
    2 (do (.disableStyle current-shape)
          (q/fill shape-color (q/map-range (q/dist (q/mouse-x) (q/mouse-y) pos-x pos-y) 0 max-dist 255 0)))
    3 (do (.disableStyle current-shape)
          (q/fill shape-color (q/map-range (q/dist (q/mouse-x) (q/mouse-y) pos-x pos-y) 0 max-dist 0 255)))))

(defn calc-pos-x [x tile-width]
  (+ (* tile-width x) (/ tile-width 2)))

(defn calc-pos-y [y tile-height tile-width]
  (+ (* tile-height y) (/ tile-width 2)))

(defn distance-from-mouse-y [y]
  (- (q/mouse-y) y))

(defn distance-from-mouse-x [x]
  (- (q/mouse-x) x))

(defn calc-angle [x y shape-angle]
  (+ (q/atan2 (distance-from-mouse-y y) (distance-from-mouse-x x)) (q/radians shape-angle)))

(defn produce-tiles [tile-count tile-width tile-height shape-angle]
  (for [y (range tile-count)
        x (range tile-count)] (let [pos-x (calc-pos-x x tile-width)
                                    pos-y (calc-pos-y y tile-height tile-width)
                                    angle (calc-angle pos-x pos-y shape-angle)] [pos-x pos-y angle])))

(defn draw-shapes [shape-size current-shape shape-angle size-mode fill-mode shape-color max-dist pos-x pos-y angle]
  (let [new-shape-size (get-shape-size size-mode shape-size pos-x pos-y)]
    (do (luk-and-fill fill-mode current-shape shape-color pos-x pos-y max-dist)
        (q/push-matrix)
        (q/translate pos-x pos-y)
        (q/rotate angle)
        (q/shape-mode :center)
        (q/no-stroke)
        (q/shape current-shape 0 0 new-shape-size new-shape-size)
        (q/pop-matrix))))

(defn draw-state [state]
  (q/background 255)
  (q/smooth)
  (let [{:keys [shape-size size-mode current-shape shape-angle tile-count tile-width tile-height fill-mode shape-color max-dist]} state]
    (doall (map #(apply draw-shapes shape-size current-shape shape-angle size-mode fill-mode shape-color max-dist %) (produce-tiles tile-count tile-width tile-height shape-angle)))))

(defn key-released [a b]
  (case (str (q/raw-key))
    ("c" "C") (assoc (q/state) :fill-mode (mod (+ (:fill-mode (q/state)) 1) 4))
    ("d" "D") (assoc (q/state) :size-mode (mod (+ (:size-mode (q/state)) 1) 3))
    ("g" "G") (let
               [tile-count (if (> (+ (:tile-count (q/state)) 5) 20) 10 (+ (:tile-count (q/state)) 5))
                tile-width (/ (q/width) (float (:tile-count (q/state))))
                tile-height (/ (q/height) (float (:tile-count (q/state))))]
                (assoc (q/state) :tile-height tile-height :tile-count tile-count :tile-width tile-width))
    ("1") (assoc (q/state) :current-shape (q/load-shape "module_1.svg"))
    ("2") (assoc (q/state) :current-shape (q/load-shape "module_2.svg"))
    ("3") (assoc (q/state) :current-shape (q/load-shape "module_3.svg"))
    ("4") (assoc (q/state) :current-shape (q/load-shape "module_4.svg"))
    ("5") (assoc (q/state) :current-shape (q/load-shape "module_5.svg"))
    ("6") (assoc (q/state) :current-shape (q/load-shape "module_6.svg"))
    ("7") (assoc (q/state) :current-shape (q/load-shape "module_7.svg"))
    (q/state)))

(defn key-pressed [a b]
  (case (q/key-as-keyword)
    :up (assoc (q/state) :shape-size (+ (:shape-size (q/state)) 5))
    :down (assoc (q/state) :shape-size (max (- (:shape-size (q/state)) 5) 5))
    :left (assoc (q/state) :shape-angle (- (:shape-angle (q/state)) 5))
    :right (assoc (q/state) :shape-angle (+ (:shape-angle (q/state)) 5))
    (q/state)))

(q/defsketch reaction-grid
  :title "You spin my circle right round"
  :size [600 600]
  :setup setup
  ;:update update-state
  :draw draw-state
  :key-pressed key-pressed
  :key-released key-released
  :features [:keep-on-top]
  :middleware [m/fun-mode])
