(ns seating.db)

(defn get-saved-chart []
  {"C, Alice" :1-beijing
   "C, Bob" :1-beijing
   "C, Cathy" :1-beijing
   "C, Dave" :2-nanjing
   "C, Ellen" :2-nanjing
   "C, Frank" :2-nanjing})



(defn get-saved-arrivals []
  #{"C, Ellen"
    "C, Dave"})

