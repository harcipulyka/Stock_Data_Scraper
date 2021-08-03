# Finviz and Stocktwits scraper
This program collects 80 variables about a ticker by scraping Finviz and Stocktwits. It divides the input into multiple threads, making it possible to scrape many thousand tickers at a time. The number of threads to use can be changed by changing the `THREADCOUNT` constant.
## Dependencies
It uses htmlunit and minimal-json for scraping the net and parsing the json. I recommend htmlunit version 2.46 because I had problems with the newer version.
Jar links: [htmlunit version 2.46](https://mvnrepository.com/artifact/net.sourceforge.htmlunit/htmlunit/2.46.0 "htmlunit version 2.46") and [minimal-json](https://jar-download.com/artifacts/com.eclipsesource.minimal-json "minimal-json")
## Usage
Check the demo method in Main for a working example.
### Finviz
For scraping finviz data you have to use the `Finviz` constructor, that requires a list of tickers (strings, not case sensitive). Then you have to run it or start it through the thread, after which you can acces the `Finviz.data` which contains a list of `Ticker` objects. Each `Ticker` has a public HashMap which contains all the data scraped from finviz. This includes the 72 variables in the main table and additionaly the industry and the sector of the company.
### Stocktwits
Similarly to finviz, you have to make a `StocktwitsScraper` class and after running, you can acces the `StocktwitsScraper.result` which contains a list of `Data` classes. Each `Data` class contains the information scraped from stocktwits about that ticker.  This consists of the following:
- String ticker - the ticker of the company
- boolean found - whether stocktwits keeps track of the company or not
- int trending - 2 if it didn't find this data, 0 if it was true, 1 if it was false
- float trendingScore - if it didn't found it, it is set to `Variables.undefinedFloat`
- float msgVolume - if it didn't found it, it is set to `Variables.undefinedFloat`
- int followers - number of followers
- float sentiment

## Output
Originally I wrote this program to run it periodically on a Raspberry Pi before market open (that's why it was important to keep it lightweight). Because of that there is a function `appendGoodFile` which takes the list of `Data` files and writes it out to an existing csv file. This method marks the date, at which it added a new colum in the first row. If you want to start a new file, you have to start it by making the file and copying the list of tickers you will scraper. This will be the first column. After that all the data will be recorded into that file at each run.
