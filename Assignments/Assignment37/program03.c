// WAP whic accept string from user and one character,
// Return index of first occurance of that Character

#include<stdio.h>


int FirstOccurance(char *str, char Char)
{
    int iCount1 = 0, iCount2 = 0;

    while (*str != '\0')
    {
        if(*str == Char)
        {
            iCount1++;
            break; 
        }
        str++;
        iCount2++;
    }
    if(iCount1 > 0)
    {
        return iCount2;
    }
    else
    {
        return -1;
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

    iRet = FirstOccurance(Arr,Ch);

    printf("Character location is : %d", iRet);
     
    return 0;
}
