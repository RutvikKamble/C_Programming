// WAP which accept string from user and count number of Capital character
#include<stdio.h>

void CountCapital(char Str[])
{    
    int iCount = 0;

    while(*Str != '\0')
    {
        if(*Str >= 'A' && *Str <= 'Z')
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