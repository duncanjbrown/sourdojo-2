Install dependencies: `npm install`

Configure the app: copy `config.example.edn` to `config.edn` and populate.

Run the app: `shadow-cljs clj-run sourdojo.build/watch`. app will be served on `localhost:3000`, tests on `localhost:8021`.

Hot-reload the tests by invoking `shadow-cljs watch test` in a separate shell.

Build for production: `shadow-cljs clj-run sourdojo.build/release prod`

Apply standard formatting: `clojure -Sdeps '{:deps {cljfmt {:mvn/version "0.6.4"}}}' -m cljfmt.main fix src/sourdojo`
