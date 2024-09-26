# Searching in OpenSearchClient

OpenSearchClient is a bean that can be used at any part of the code.
Ensure that the next properties are defined in your `application.properties`:

```
opensearch.scheme=https
opensearch.server=localhost
opensearch.port=9199
opensearch.user=admin
opensearch.password=*****
opensearch.truststore.path=../cert/opensearch-truststore
opensearch.truststore.password=*****
```

And for searching, you can rely on `SearchQuery<I>` class. Where `<I>` is the class you are looking for on OpenSearch.

SearchQuery is composed by:

## The class

The element that is searched. Any other object that does not fits this class will be ignored.

## Searching using SearchParameters

This class tries to help the programmer to implement OpenSearch queries easily.

It is composed by:

### SearchParameters Fields

You can search by any combination of:

- search: that is a pair of field-value. The field must match the selected value.
- multi-search: that is a list of pairs with one value. Any field on the list must match the selected value.
- ranges: a parameters can have a value between a range. Combine the actions `lt`, `lte`, `gt`, `gte`.
- fuzziness: parameters that can have some differences between the selected value. Select the fuzziness method (default
  is `AUTO`), and the expansions (how far is from the original value in characters), and the prefixLength (forces the
  first characters to match the value).

### SearchParameters Actions

Previous parameters generate a match, but the action of the match depends on the class used.
You can combine any SearchParameter as you wish.

A list of actions is:

#### MustHave

Elements that must be present on the results.

#### MustNotHave

Elements that must not be present on the results.

#### ShouldHave

Elements that are good to have. The more that they have, the higher the score.

This class has the property `minimumShouldMatch` that indicates the minimum number of elements in the should query that
must be present on a result.
If multiples `ShouldHave` elements are present, the system will only use the maximum value.

#### SearchFilter

A filter applied after the search. To filter the results and get only a subset.

### Intervals

Intervals allow searching the words in the values, but separated by X words.
That means that it is no need to be correlative words.
By default, the system allows a distance of 50 elements between two words.

- prefix: forces the prefix to be present in the field.
- intervalMatch.query: the words that must be present on the field.
- intervalMatch.maxGap: distance allowed between two words from the query.
- intervalMatch.ordered: if true, the words must respect the order.
- intervalWildcards.pattern: search for a pattern. Can be combined with `intervalMatch`.
- intervalsSearchOperator: if you wish to match `all` the defined rules or `any` of them.

### Pagination

Two parameters `from` and `size` implements a standard pagination system.