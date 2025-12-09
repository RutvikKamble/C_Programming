// WAP which accept string from user and copy that characters of that string into
// another string by removing all whide spaces.


#include<stdio.h>

void StrCpy(char *str1, char *str2)
{

    while(*str1 != '\0')
    {
        if(*str1 != ' ')
        {
            *str2 = *str1;
            str1++;
            str2++;
        }
        else
        {
            str1++;
        }
    }
    *str2 = '\0';
}

int main()
{
    char Arr[50] = "Marvellous Python";
    char Brr[50];

    StrCpy(Arr,Brr);

    printf("%s",Brr);

    return 0;
}