(defproject sitges "0.1.0"
  :description "Sitges festival parser"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [enlive "1.0.0-SNAPSHOT"]
		 [sqlitejdbc "0.5.6"]
                 [ring "0.2.5"]
		 [compojure "0.6.4"]
                 [net.cgrand/moustache "1.0.0-SNAPSHOT"]]
  :dev-dependencies [[swank-clojure "1.3.0-SNAPSHOT"]
		     [lein-ring "0.4.5"]
		     [ring-serve "0.1.0"]]
  :source-path "src"
  :main sitges.core)
