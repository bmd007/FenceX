### use maven instead of local library
### kafka streams idle instance research
### add metric for 
 - successful data base inserts
 - errors while inserting in data base
 - errors while querying data base
 
 ### think about making poll leg fully reactive: 1-reactive kafka instead of kafka streams, 2-still h2 but with r2dbc, 3-location update topic compact log, 4-if table is empty, subscribe to offset zero of location update topic