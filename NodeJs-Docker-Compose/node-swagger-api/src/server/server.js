
/*
server.js

Purpose:
  creating a connection and communication
  between the application and database
  for the properties table.
*/

//let the file require these packages/files
var express = require('express');
var mysql = require('mysql');
var fs = require("fs");

//reading properties file into json object
//var sqlProp = JSON.parse(fs.readFileSync('./server/properties.md'));
var sqlProp =
{
    host            : process.env.DATABASE_HOST,
    port            : process.env.MYSQL_PORT,
    user            : process.env.MYSQL_USER,
    password        : process.env.MYSQL_PASSWORD,
    database        : process.env.MYSQL_DATABASE
};

var cRes = -1;



/*
getSQL

Purpose:
  to retrieve all the records from
  properties and set the res with
  all the rows formated in a [json] obj.
*/
function getSQL(req, res)
{
  //making database connection based on properties.md
  var connection = mysql.createConnection(sqlProp);
  console.log('Server Connected');
/*
  //connect to the specited database
  connection.connect(function(error)
  {
      if(!!error)
        console.log('Server connection Error');
      else
        console.log('Server Connected');
  });
*/
  //making connection query
  connection.query("select * from properties", function(error, rows, fields)
  {
    if(!!error)
    {
      console.log('Server: Error in the database connection');
      connection.end();
      console.log('Connection to database closed');
    }
    else
    {
      console.log('Server: Successful query')
      connection.end();
      console.log('Connection to database closed');
      res.json(rows);
    }
  });
}


/*
insertSQL

Purpose:
  create a sql statement of the values
  we want for the new insert into the
  table properties. Create a Connection
  query with the sql statement, and
  insert the new record in the table.
*/
function insertSQL(res, values)
{
  //making database connection based on properties.md
  var connection = mysql.createConnection(sqlProp);
  console.log('Server Connected');
/*
  //connect to the specited database
  connection.connect(function(error)
  {
      if(!!error)
        console.log('Server connection Error');
      else
        console.log('Server Connected');
  });
*/
  //building the sql statement
  var sqlState = "insert into properties (id, address, city, state, zip) values (";
  sqlState += "NULL, '" + values[0] + "', '" + values[1] + "', '" + values[2] + "', '" + values[3];
  sqlState += "')";


  //making connection query
  connection.query(sqlState, function(err, dRes, fields)
  {
    if (err)
    {
      console.log('Server: Error in the connection query');
      //res.status(400);
      //res.json([{"message":"Error in the connection when fetching query"}])
      connection.end();
      console.log('Connection to database closed');
    }else{
      console.log('Server: Successful insert');
      connection.end();
      console.log('Connection to database closed');
      res.json([{"message":"added"}]);
    }
  });
}


/*
updateSQL

Purpose:
  create a sql statement of the values
  that will update a record in the
  table properties. Create a Connection
  query with the sql statement, and
  update the record in the table.
*/
function updateSQL(res, id, values)
{
  //making database connection based on properties.md
  var connection = mysql.createConnection(sqlProp);
  console.log('Server Connected');

/*
  //connect to the specited database
  connection.connect(function(error)
  {
      if(!!error)
        console.log('Server connection Error');
      else
        console.log('Server Connected');
  });
  */
  //building the sql statement
/*

  sqlState += " SET address = '" + values[1] + "', city = '" + values[2] + "', state = '" + values[3] + "', zip = '" + values[4] + "'";
  sqlState += " WHERE id = " + values[0];
*/

  for(key in values)
  {

    var sqlState = "UPDATE properties set " + key + " = '";
    sqlState += values[key] + "' where id = " + id;

    //making connection query
    connection.query(sqlState, function(err, dRes, fields)
    {
      if (err)
      {
        console.log('Server: Error in the connection query');
        connection.end();
        console.log('Connection to database closed');
        res.status(400);
        res.json([{"message":"Error in the connection when fetching query"}])
      }
      else
      {
        console.log('Connection to database closed');
      }
    });
  }

  res.json([{"message":"updated"}]);
}


/*
getID

Purpose:
  builds a sql statement for the
  specified id. creates a Connection
  query where we will either set the
  res value with the record as a json
   or set res status to 404, and ends
   the connection to update the user
   with the error.
*/
function getID(res, id)
{
  //making database connection based on properties.md
  var connection = mysql.createConnection(sqlProp);
  console.log('Server Connected');

  /*
  //connect to the specited database
  connection.connect(function(error)
  {
      if(!!error)
        console.log('Server connection Error');
      else
        console.log('Server Connected');
  });
  */
  //building sql statement
  var sqlState = "select * from properties where id =" + id;

  //making connection query
  connection.query(sqlState, function(err, dRes, fields)
  {
    //console.log(dRes);
    if(dRes.length == 0)
    {
      console.log("Server: getID Error");
      connection.end();
      console.log('Connection to database closed');
      res.status(404);
      res.json({message:"Error Not found"});
    }
    else
    {
      console.log("Server: getID Success");
      connection.end();
      console.log('Connection to database closed');
      res.json(dRes[0]);
    }
  });

};

/*
deleteID

Purpose:
  builds a sql statement for the
  specified id. creates a Connection
  query where we will either delete the
   record from the database and set
   res with a json delete message or set res status
   to 404, and end the connection to update
   the user with the error.
*/
function deleteID(res, id)
{
  //making database connection based on properties.md
  var connection = mysql.createConnection(sqlProp);
  console.log('Server Connected');
  /*
  //connect to the specited database
  connection.connect(function(error)
  {
      if(!!error)
        console.log('Server connection Error');
      else
        console.log('Server Connected');
  });
 */
  //building sql statement
  var sqlState = "delete from properties where id = " + id;

  //making connection query
  connection.query(sqlState, function(err, dRes, fields)
  {

    //console.log("affectedRows: ", dRes.affectedRows);

    //if no rows were affected in database
    if(dRes.affectedRows == 0)
    {
      //not a successful delete
      console.log("Server: delete {id} Not Found");
      connection.end();
      console.log('Connection to database closed');
      res.status(404);
      res.json({message:"Error Not found"});
    }
    else
    {
      //successful delete if > 0 rows affected
      console.log("Server: delete Success");
      connection.end();
      console.log('Connection to database closed');
      res.json([{"message":"deleted"}])
    }

  });

};



function checkID(id, callback)
{
  //making database connection based on properties.md
  var connection = mysql.createConnection(sqlProp);
  console.log('Server Connected');
  /*
  //connect to the specited database
  connection.connect(function(error)
  {
      if(!!error)
        console.log('Server connection Error');
      else
        console.log('Server Connected');
  });
  */
  //building sql statement
  var sqlState = "select * from properties where id =" + id;

  //making connection query
  connection.query(sqlState, function(err, dRes, fields)
  {
    //console.log(dRes);
    if(dRes.length == 0)
    {
      console.log("Server: checkID: No id found");
      connection.end();
      console.log('Connection to database closed');
      callback(-1);
    }
    else
    {
      console.log("Server: checkID: id is found");
      connection.end();
      console.log('Connection to database closed');
      callback(1);
    }
  });

};


/*
allows the functions to be seen by
other modules/js files.
*/
module.exports =
{
  getSQL: getSQL,
  insertSQL: insertSQL,
  updateSQL: updateSQL,
  getID: getID,
  deleteID: deleteID,
  checkID: checkID
};
