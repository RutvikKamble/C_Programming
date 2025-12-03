// WAP which accept string from user and count number of small character
#include<stdio.h>

void CountCapital(char Str[])
{    
    int iCount = 0;

    while(*Str != '\0')
    {
        if(*Str >= 'a' && *Str <= 'z')
        {
            iCount++;
        }
        Str++;
    }
    printf("The total Capital letters are : %d",iCount);
}

int main()
{
    int iRet = 0;
    char Arr[30] = {"\0"};
    
    printf("Enter the string : \n");
    scanf("%[^'\n']s",Arr);

    CountCapital(Arr);

   
    return 0;
}