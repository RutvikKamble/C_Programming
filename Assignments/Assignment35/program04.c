// WAP which accept string from user and
// Check whether it contain vowels or not

#include<stdio.h>
#include<stdbool.h>

bool CheckVowel(char Str[])
{    
    int iCnt = 0;

    while (*Str != '\0')
    {
        if(*Str == 'a' || *Str == 'e' || *Str == 'i' || *Str == 'o' || *Str == 'u' ||
        *Str == 'A' || *Str == 'E' || *Str == 'I' || *Str == 'O' || *Str == 'U')
        {
            return true;
        }
        Str++;
    }
}

int main()
{
    bool bRet = false;
    char Arr[30] = {"\0"};
    
    printf("Enter the string : \n");
    scanf("%[^'\n']s",Arr);

    bRet = CheckVowel(Arr);

    if(bRet == true)
    {
        printf("Yes, The String contain vowel");
    }
    else
    {
        printf("No, The string is Not contain vowel");
    }

    return 0;
}