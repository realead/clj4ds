# clj2ds

Me working through Henry Garner's "Clojure for data sience".

Some examples are typed down verbatim, some are changed slightly - everything what pervents me from going too fast through the pages.

### Dependencies
   
   * Cojure
   * leiningen (https://leiningen.org/)
   * some data sets need to be downloaded

### Chapter 1

   * Download http://www.complex-systems.meduniwien.ac.at/elections/ElectionData/UK2010.xls and http://www.complex-systems.meduniwien.ac.at/elections/ElectionData/Russia2011.zip
   * extract and put the downloaded data into chapter1/data
   * `cd chapter1`
   * `lein run` for running all examples
   * `lein test` for running unit-tests

### Chapter 2

   * Download files as noted in chapter2/data/README.md
   * extract and put the downloaded data into chapter2/data
   * `cd chapter2`
   * `lein run` for running all examples

### Chapter 3

   * Download files as noted in chapter3/data/README.md
   * extract and put the downloaded data into chapter3/data
   * `cd chapter3`
   * `lein run` for running all examples

### Chapter 4

   * Download files as noted in chapter4/data/README.md
   * extract and put the downloaded data into chapter4/data
   * `cd chapter4`
   * `lein run` for running all examples
   * for running only some examples use `lein repl`, and now
       - `(def titanic (chapter4.data/titanic-data))` for loading data
       - `(ex-4-33 titanic)` to run an example
       - `(use 'chapter4.examples :reload)` to reload examples.clj

### Chapter 5

   * Download files as noted in chapter5/data/README.md
   * extract and put the downloaded data into chapter5/data
   * `cd chapter5`
   * `lein run` for running all examples
   * for running only some examples use `lein repl`, and now
       - `(def soi (chapter5.data/soi-data))` for loading data
       - `(ex-5-xx soi)` to run an example
       - `(use 'chapter5.examples :reload)` to reload examples.clj

### Chapter 6

ToDo



