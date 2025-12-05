// WAP whic accept string from user and one character,
// Return frequency of that character

#include<stdio.h>


int CountFrequency(char *str, char Char)
{
    int iCount = 0;

    while (*str != '\0')
    {
        if(*str == Char)
        {
            iCount++;
        }
        str++;
    }
    
    return iCount;    
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

    iRet = CountFrequency(Arr,Ch);

    printf("The Frequency of %c is : %d", Ch, iRet);
     
    return 0;
}