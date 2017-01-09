(defproject latte-integers "0.5.1-SNAPSHOT"
  :description "A formalization of integers in LaTTe."
  :url "https://github.com/fredokun/latte-integers.git"
  :license {:name "MIT Licence"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [latte "0.4.1-SNAPSHOT"]
                 [latte-sets "0.1.0-SNAPSHOT"]]
  :codox {:output-path "docs/"
          :metadata {:doc/format :markdown}
          :namespaces [latte-integers.core latte-integers.nat
                       latte-integers.rec latte-integers.plus
                       latte-integers.minus latte-integers.ord]}
  :plugins [[lein-codox "0.10.1"]]
  :profiles {:prof {:jvm-opts ["-Dcom.sun.management.jmxremote"
                               "-Dcom.sun.management.jmxremote.ssl=false"
                               "-Dcom.sun.management.jmxremote.authenticate=false"
                               "-Dcom.sun.management.jmxremote.port=43210"]}})

