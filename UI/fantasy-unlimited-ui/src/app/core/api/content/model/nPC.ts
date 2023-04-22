/**
 * FantasyUnlimited API
 * Fantasy Unlimited - REST API Description
 *
 * The version of the OpenAPI document: v0.0.1
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { Quest } from './quest';


export interface NPC { 
    id?: string;
    name?: string;
    description?: string;
    iconName?: string;
    raceId?: string;
    classId?: string;
    level?: number;
    title?: string;
    vending?: boolean;
    selling?: { [key: string]: number; };
    genericDialogue?: Array<string>;
    questIds?: Array<string>;
    sellingItems?: { [key: string]: number; };
    quests?: Array<Quest>;
}

