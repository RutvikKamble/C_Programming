// WAP whic accept string from user and one character,
// Check whether that character is present in string or not

#include<stdio.h>

#define TRUE 1
#define FALSE 0

typedef int BOOL;

BOOL CheckChar(char *str, char Char)
{
    int iCount = 0;

    while (*str != '\0')
    {
        if(*str == Char)
        {
            iCount++;
        }
        *str++;
    }
    if (iCount > 0)
    {
        return TRUE;
    }
    else
    {
        return 0;
    }
}


int main()
{
    char Arr[50] = {'\0'};
    char Ch = '\0';
    int iRet = 0;


    printf("Enter the String : ");
    scanf("%s",&Arr);

    printf("Enter the Character : ");
    scanf(" %c",&Ch);

    iRet = CheckChar(Arr,Ch);

    if(iRet == 1)
    {
        printf("%d",TRUE);
    }   
    else
    {
        printf("%d",FALSE);
    } 

    return 0;
}