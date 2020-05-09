Install dependencies: `npm install`

Configure the app: copy `config.example.edn` to `config.edn` and populate.

Run the app: `npm run watch`. app will be served on `localhost:3000`, tests on `localhost:8021`.

Hot-reload the tests by invoking `shadow-cljs watch test` in a separate shell.

Build for production: `npm run build`

Deploy to S3: `npm run deploy`

Apply standard formatting: `npm run cljfmt`
