run:
	clojure -A:dev -m figwheel.main -b dev -r

.PHONY: dist
dist:
	clojure -A:dev -m figwheel.main --optimizations advanced --build-once dev
	mkdir -p dist/cljs-out
	cp resources/public/* dist
	cp target/public/cljs-out/dev-main.js dist/cljs-out

gh-pages-init:
	git checkout --orphan gh-pages

gh-pages:
	# See https://gist.github.com/cobyism/4730490;
	# https://gist.github.com/cobyism/4730490#gistcomment-1961457
	rm -rf target dist
	mkdir dist

	git clone .git --branch gh-pages dist

	make dist

	(cd dist && git add --all && git commit -m "Publishing to gh-pages" && git push origin gh-pages)

	git push origin gh-pages
