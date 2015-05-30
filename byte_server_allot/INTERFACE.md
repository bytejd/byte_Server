URL/api/interface/method
request:
______________
|method|param|paramDesc|


response:
{
    code:
    content:
    {
    }
}


GetAccessServer

interface:server
|method         |param      |paramDesc          |required
|getServer      |uid        |required           |true
|               |serverType |int serverTypeID   |true
|               |devType    |int 0:ios 1:android|true
|               |sysVer     |string             |true
|               |devKey     |string             |true
|               |appVer     |string             |true

rsp:
{   
    code:8200
    content:{
        schema:http
        ip:192.168.1.1
        port:443
    }
}
