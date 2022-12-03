# KlusterMap

Test task and simple demonstration for map clustering from CSV file.

# Structure

No modules due to the lack of needs, but the Clean Approach would help easily separate logic and reuse with some difference. Like for Huawei store or
to implement a Realm DB.

# .gradle

There is an over-engineered approach. But it's good for demonstration and future decoupling to modules.

# UI

There wasn't requests for some specific design or elements, so decided to leave it very simple.

# Common

The working set is limited to 10k. It work with much bigger sets too, but i see no reason to put the whole world in one variable.

# Bottlenecks

The CSV file. The devil called, asked to return. It's a request to use it as it is. But maybe it would be better to make a pre-filled db file at
least. Or make a separate files, with partition by regions or countries.

The server approach, with paging and so on is not under discussion.

Biggest sadness that we can't remove the file from 'raw' or 'assets' and decrease the app size.