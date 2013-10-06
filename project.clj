(defproject reditore "0.9.1"
  :description "Ring session store implemented on top of redis."
  :url "https://github.com/schovi/reditore"
  :license {:name "MIT License"
            :url "http://www.opensource.org/licenses/mit-license.php"
            :distribution :repo}
  :dependencies [[org.clojure/clojure "1.2.1"]
                 [ring/ring-core "1.2.0-beta2"]
                 [com.taoensso/carmine "2.2.1"]
                 [org.clojure/data.json "0.2.3"]])