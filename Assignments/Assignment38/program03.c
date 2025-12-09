// WAP which accept string from user and copy that characters of that string into
// another string by by converting all small character into capital character;

#include<stdio.h>

void StringConvert(char *Str1, char *Str2)
{
    while (*Str1 != '\0')
    {
        if(*Str1 >= 'a' && *Str1 <= 'z')
        {
            *Str2 = *Str1 - 32;
            Str1++;
            Str2++;
        }
        else
        {
            *Str2 = *Str1;
            Str1++;
            Str2++;
        }
    }    
    *Str2 = '\0';
}

int main()
{
    char Arr[50] = {('\0')};
    char Brr[50] = {('\0')};

    printf("Enter the String : ");
    scanf("%[^'\n']s",Arr);

    StringConvert(Arr,Brr);

    printf("Updated String is : %s ",Brr);

    return 0;
}