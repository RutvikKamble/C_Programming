// WAP whic accept string from user and one character,
// Return index of last occurance of that Character


#include<stdio.h>

int FirstOccurance(char *str, char Char)
{
    int iCount1 = 0, iCnt = 0;

    while (*str != '\0')
    {
        iCount1++;
        str++;
    }

    for(iCnt = iCount1; iCnt > 0; iCnt--)
    {
        if (str[iCount1] == Char)
        {
            return iCnt;
        }   
        else
        {
            return -1;
        }
        str--;
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
