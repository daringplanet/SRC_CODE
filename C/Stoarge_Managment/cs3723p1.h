/**********************************************************************
cs3723p1.h
Copyright 2018 Larry Clark,  this code cannot be copied to any other website 
Purpose:
    Defines constants for
        boolean values
        maximum sizes
        program return codes
        error messages
    Defines typedefs for
        MetaAttr - describes one attribute within a node type
        NodeType - describes one node type
        AllocNode represents an allocated node.  The actual size of an 
            allocated item may be much larger.  The size of an allocated 
            item cannot be less than the size of a FreeNode structure.
        FreeNode - represents a free node.  The actual size of the node
            will most likely be larger than the FreeNode structure.
        StorageManager is the primary structure used by this program.
        SMResult is used by the smrc... functions to 
            specify whether they executed successfully.
    Prototypes
Notes:
    
**********************************************************************/
#define TRUE 1
#define FALSE 0

#define MAX_KEY_SIZE 10                     // Maximum size of a key for Hash Table
#define MAX_MESSAGE_SIZE 100                // Maximum message size for smResult
#define MAX_STRING 30                       // Maximum size of strings like 
                                            // node type names, attribute names
#define MAX_NODE_TYPE 5	                    // Maximum number of node types 
#define MAX_NODE_ATTR 50                    // Maximum number of combined node attr
#define MAX_DATA_SZ 500                     // Maximum size of sbData
#define ERROR_PROCESSING 3                  // error during processing - exit value

#define NOT_FOUND -1                        // Could not find name in metadata

// Errors returned in the rc of SMResult
#define RC_NOT_AVAIL 901            // There isn't any free memory to handle alloc request
#define RC_CANT_FREE 902            // Attempted to free a node which isn't allocated.
#define RC_INVALID_ADDR 903         // Invalid address which isn't within heap
#define RC_ASSOC_ATTR_NOT_PTR 801   // Attribute name for ASSOC not a pointer attribute
#define RC_ASSOC_ATTR_NOT_FOUND 802 // Attribute name for ASSOC not found for the from node

// MetaAttr describes an attribute within a node type
typedef struct MetaAttr
{
    short  shNodeType;                      // Type of node
    char   szAttrName[MAX_STRING+1];        // Name of the attribute
    char   cDataType;                       // Data type: S - char string, P -Ptr, D - double, I - int
    short  shSizeBytes;                     // size in bytes including zero byte for strings
    short  shOffset;
}MetaAttr;
// NodeType describes one type of node
typedef struct NodeType
{
    char szNodeTypeNm[MAX_STRING+1];
    short shBeginMetaAttr;              // Subscript in metaAttrM of first attribute for
                                        // this node type.
    short shNodeTotalSize;
}NodeType;

// AllocNode represents an allocated node.  The valus of shAllocSize is never as 
// big as sizeof(AllocNode).
typedef struct AllocNode
{
    short shAllocSize;                 // total size of the allocated item.  
    char  cAF;                         // A - allocated, F - free
    short shRefCount;                  // The number of references to this allocated node.
    short shNodeType;                  // Node Type subscript.
    char  sbData[MAX_DATA_SZ];         // This is the user's data in the node.  It might
                                       // be bigger than MAX_STRING.
} AllocNode;

// FreeNode represents an free node.  The value of shAllocSize must be at least
// sizeof(FreeNode).     
typedef struct FreeNode
{
    short shAllocSize;                 // total size of the free item. 
    char  cAF;                         // A - allocated, F - free    
    struct FreeNode *pNextFree;        // Points to the next free node
} FreeNode;
// StorageManager is the primary structure used by this program.  
typedef struct
{
    int iHeapSize;                       // Total size of the heap memory being managed
    short shMinimumNodeSize;             // Minimum size of any allocated node.
    short shPrefixSize;                  // Size of the prefix portion of an allocated node
    char *pBeginStorage;                 // Beginning of the heap memory being managed
    char *pEndStorage;                   // End address immediately after the heap memory
    char *pFreeTop;                      // Top of Free Memory
    FreeNode *pFreeHeadM[MAX_NODE_TYPE]; // An array of free lists (one for each NodeType)
    NodeType nodeTypeM[MAX_NODE_TYPE];   // array of node types
    MetaAttr metaAttrM[MAX_NODE_ATTR];   // array of attribute meta data
} StorageManager;

// This is returned by most of the storage management functions via a parameter.  
typedef struct
{
    int rc;                                // Return Code is 0 if it is normal.  Otheriwise,
                                           // it is not zero.  See the defined constants.
    char szErrorMessage[MAX_MESSAGE_SIZE + 1];  // If a problem is encountered, this should 
                                                // explain the error.
} SMResult;

// student functions
void * userAllocate(StorageManager *pMgr
    , short shUserDataSize, short shNodeType, char sbUserData[], SMResult *psmResult);
void userRemoveRef(StorageManager *pMgr, void *pUserData, SMResult *psmResult);
void userAssoc(StorageManager *pMgr, void *pUserDataFrom, char szAttrName[]
    , void *pUserDataTo, SMResult *psmResult);
void userAddRef(StorageManager *pMgr, void *pUserDataTo, SMResult *psmResult);
void memFree(StorageManager *pMgr, AllocNode *pAlloc, SMResult *psmResult);

#if defined(_WIN32) || defined(_WIN64)    
extern void printNode(StorageManager *pMgr, void *pUserData);
#else
extern "C" void printNode(StorageManager *pMgr, void *pUserData);    
#endif

// Driver functions
void utilPrintMeta(StorageManager *pMgr);

void utilPrintFree(StorageManager *pMgr);
short findNodeType(StorageManager *pMgr, char szNodeTypeNm[]);
AllocNode * utilAlloc(StorageManager *pMgr, short shTotalSize);
void utilInit(StorageManager *pMgr);
void utilDump(StorageManager *pMgr);

void errExit(const char szFmt[], ... );

// Simple macro for converting addresses to unsigned long
#define ULAddr(addr) ((unsigned long) addr)