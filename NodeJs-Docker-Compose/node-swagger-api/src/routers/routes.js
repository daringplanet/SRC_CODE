const express = require('express');
const server = require('../server/server.js');
//const cors = require('cors');
const router = express.Router();
/*
var corsOptions =
{
  "api-key": function (apiKey, callback)
  {
    console.log("Hello World");
    if (apiKey == "cs4783FTW")
    {
      callback(null, true)
    } else
    {
      callback(new Error('Not allowed by CORS'))
    }
  }
}
*/

/**
 * @swagger
 * /hello:
 *  get:
 *    description: Use to greet user
 *    responses:
 *      '200':
 *        description: A successful response
 */
router.get('/hello', (req, res) =>
{
  res.json([{"message":"hello yourself"}]);
});

/**
 * @swagger
 * /properties:
 *  get:
 *    description: Use to retrieve all the records in the table properties
 *    responses:
 *      '200':
 *        description: A successful response
 */
router.get('/properties', (req, res) =>
{
	//get records/updates the return res.
	server.getSQL(req, res);
});



/*
get/properties/:id

Purpose:
	gets the id from the parameters.
	returns the json object of the record
	with the specified id and a 200 response urlencoded
	 or a 404 response if there was an invalid query.

*/
/**
 * @swagger
 * /properties/{id}:
 *  get:
 *     parameters:
 *      - in: path
 *        name: id
 *        required: true
 *        schema:
 *          type: integer
 *          minimum: 1
 *        description: The user ID for rechord in properties
  *     description: Will get the specified record by its id
 *     responses:
 *      '200':
 *        description: A successful response
 *      '404':
 *        description: Error Not found
 *      '400':
 *        description: invalid integer
 */
router.get('/properties/:id', (req, res, next) =>
{
	//get the id
	var id = req.params.id;

  if(isNaN(parseInt(id)))
  {
    res.status(400);
    res.json({message:"Invalid integer"});
  }else
  {
  //console.log('need to get ', id);

	//get record/updates the return res.
	server.getID(res, id);
}
});




/*
	post request for properties

	Discription:
		will read in the values from the body
		and check to make sures all the values
		are valid. If NOT we respond with a 400
		response code and a [json] with details
		about which part(s) were invalid and
		why. Otherwise we will insert a new
		record in the properties table with the
		given values in the body of the request.
*/



/**
 * @swagger
 * /properties:
 *  post:
 *     parameters:
 *      - in: header
 *        name: api-key
 *        type: string
 *        description: The authorization header
 *     requestBody:
 *       content:
 *         application/x-www-form-urlencoded:
 *           schema:
 *             type: object
 *             properties:
 *              address:
 *                 type: string
 *                 description: the address for the new rechord
 *              city:
 *                type: string
 *                description: the city for the new rechord
 *              state:
 *                type: string
 *                description: The state for the new record
 *              zip:
 *                type: string
 *                description: The zip for the new record
 *     description: Will create and insert new record in properties table
 *     responses:
 *      '200':
 *        description: A successful response
 *      '400':
 *        description: Invalid parameters
 *      '401':
 *        description: Unauthorized user
 *     security:
 *      - ApiKeyAuth: []
 */
router.post('/properties', (req, res) =>
{

  validate(req, function(valRes)
  {
    if (valRes != true)
    {
      res.status(401);
      res.json(valRes);
      res.end();
    }
    else
    {
    	//var to build our [values]
    	var vals = [];

    	// obtain details from body
    	address = req.body.address;
    	city = req.body.city;
    	state = req.body.state;
    	zip = req.body.zip;

    	//array for error messages, if any
    	var error = [];

    	//checks to see if all parameters are valid
    	if (address.length < 1 || address.length > 200)
    		error.push({"message":"address is not between 1 and 200 characters"});

    	if(city.length < 1 || city.length > 50)
    		error.push({"message":"city is not between 1 and 50 characters"});

    	if(state.length != 2)
    		error.push({"message":"State is not 2 characters"});

    	if(zip.length < 5 || zip.length > 10)
    		error.push({"message":"zip is not between 5 and 10 characters"});

    	if(error.length == 0)  //if all parameters are valid
    	{

    		//push them in order of:
    		vals.push(address);
    		vals.push(city);
    		vals.push(state);
    		vals.push(zip);

    		//inserts record/updates the return res
    		server.insertSQL(res, vals);

    	}
    	else //we have an error
    	{
    		res.status(400);
    		res.json(error);
    	}
    }
  });
});


/**
  * @swagger
  * /properties/{id}:
  *  put:
  *     parameters:
  *      - in: header
  *        name: api-key
  *        type: string
  *        description: The authorization header
  *      - in: path
  *        name: id
  *        required: true
  *        schema:
  *          type: integer
  *          minimum: 1
  *        description: The user id for rechord in properties
  *     requestBody:
  *       content:
  *         application/x-www-form-urlencoded:
  *           schema:
  *             type: object
  *             properties:
  *              address:
  *                 type: string
  *                 description: the address for the new rechord
  *              city:
  *                type: string
  *                description: the city for the new rechord
  *              state:
  *                type: string
  *                description: The state for the new record
  *              zip:
  *                type: string
  *                description: The zip for the new record
  *     description: Will create and insert new record in properties table
  *     responses:
  *       '200':
  *         description: A successful response
  *       '400':
  *         description: Invalid parameters
  *       '401':
  *         description: Unauthorized user
  *       '404':
  *         description: Error Not found
  *     security:
  *      - ApiKeyAuth: []
  */
router.put('/properties/:id', (req, res) =>
{
  validate(req, function(valRes)
  {
    if (valRes != true)
    {
      res.status(401);
      res.json(valRes);
      res.end();
    }
    else
    {

    	//var to build our [values]
    	var vals = [];


    	// obtain details from body
    	id = req.params.id;
    	address = req.body.address;
    	city = req.body.city;
    	state = req.body.state;
    	zip = req.body.zip;

      if(isNaN(parseInt(id)))
      {
        res.status(400);
        res.json({message:"Invalid integer"});
      }
      else
      {

      	//array for error messages, if any
      	var error = [];

      	//get record to see if property exists
      	server.checkID(id, function(idCheck)
        {

        	//checks to see if all parameters are valid
        	if(idCheck ==  -1)
          {
            res.status(404);
        		res.json({"message":"Error Not found"});
          }
          else
          {

            if (typeof address != 'undefined' && address != '')
          	 if (address.length < 1 || address.length > 200)
          		error.push({"message":"address is not between 1 and 200 characters"});

            if (typeof city != 'undefined'  && city != '')
          	 if(city.length < 1 || city.length > 50)
          		error.push({"message":"city is not between 1 and 50 characters"});

            if (typeof state != 'undefined'  && state != '')
          	 if(state.length != 2)
          		error.push({"message":"State is not 2 characters"});

            if (typeof zip != 'undefined'  && zip != '')
          	 if(zip.length < 5 || zip.length > 10)
          		error.push({"message":"zip is not between 5 and 10 characters"});

          	if(error.length == 0)  //if all parameters are valid
          	{

          		//push them in order of:
              var temp = {};
              if (typeof address != 'undefined' && address != '')
                temp["address"] = address;

              if (typeof city != 'undefined'  && city != '')
                temp["city"] = city;

              if (typeof state != 'undefined'  && state != '')
                temp["state"] = state;

              if (typeof zip != 'undefined'  && zip != '')
                temp["zip"] = zip;


          		//updates record and the return res
          		server.updateSQL(res, id, temp);


          	}
          	else //we have an error
          	{
          		res.status(400);
          		res.json(error);
          	}
          }
        });
      }
    }
  });
});



/*
delete/properties/:id

Purpose:
	retreives the id from the parameters, then
	deletes the record with the specified id
	and returns a message confirming the deletion with
	a 200 response code, or a 404 response if there
	was an invalid query.

*/
/**
 * @swagger
 * /properties/{id}:
 *  delete:
 *     parameters:
 *      - in: path
 *        name: id
 *        required: true
 *        schema:
 *          type: integer
 *          minimum: 1
 *        description: The user id for rechord in properties
 *      - in: header
 *        name: api-key
 *        required: false
 *        type: string
 *        description: The authorization header
 *     description: Will delete the specified record by its id
 *     responses:
 *      '200':
 *        description: A successful response
 *      '404':
 *        description: Error Not found
 *      '401':
 *        description: Unauthorized user
 *      '400':
 *        description: invalid integer
 *     security:
 *      - ApiKeyAuth: []
 */
 router.delete('/properties/:id', (req, res, next) =>
 {

   validate(req, function(valRes)
   {
     if (valRes != true)
     {
       res.status(401);
       res.json(valRes);
       res.end();
     }
     else
     {
       //console.log(req.header('api-key'));
      	//get the id
      	var id = req.params.id;
        if(isNaN(parseInt(id)))
        {
          res.status(400);
          res.json({message:"Invalid integer"});
        }
        else
        {
      	   //console.log('need to delete ', id);
           //deletes record/updates the return res.
      	   server.deleteID(res, id);
        }
      }
   });


 });


 function validate(req, callback)
 {

     key = req.header('api-key');

      switch(key)
      {
        case 'undefined':
          callback({message:"No api-key"});
          break;
        case 'cs4783FTW':
          callback(true);
          break;
        default:
          callback({message:"Invalid api-key"});
      }
 };


module.exports = router;
