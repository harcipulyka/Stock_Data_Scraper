#### Finviz and Stocktwits scraper
This program collects 80 variables about a ticker by scraping Finviz and Stocktwits. It divides the input into multiple threads, making it possible to scrape many thousand tickers at a time. The number of threads to use can be changed by changing the `THREADCOUNT` constant.
####Dependencies
It uses htmlunit and minimal-json for scraping the net and parsing the json. I recommend htmlunit version 2.46 because I had problems with the newer version.
Jar links: [htmlunit version 2.46](https://mvnrepository.com/artifact/net.sourceforge.htmlunit/htmlunit/2.46.0 "htmlunit version 2.46") and [minimal-json](https://jar-download.com/artifacts/com.eclipsesource.minimal-json "minimal-json")
