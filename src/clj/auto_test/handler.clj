(ns auto-test.handler
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [auto-test.middleware :refer [wrap-middleware]]
            [environ.core :refer [env]]))

(def mount-target
  [:div#app
      [:h3 "ClojureScript has not been compiled!"]
      [:p "please run "
       [:b "lein figwheel"]
       " in order to start the compiler"]])

(def loading-page
  (html5
   [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1"}]
    ;; (include-js "https://fb.me/react-0.14.7.js")
    ;; (include-js "https://fb.me/react-dom-0.14.7.js")
    (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))
    (include-css "/css/autosuggest.css")]
    [:body
     mount-target
     ;;(include-js "https://cdn.rawgit.com/moroshko/react-autosuggest/847fe9d/dist/standalone/autosuggest.js")
     (include-js "/js/app.js")
]))


(defroutes routes
  (GET "/" [] loading-page)
  (GET "/about" [] loading-page)
  
  (resources "/")
  (not-found "Not Found"))

(def app (wrap-middleware #'routes))
